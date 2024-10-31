package net.projectlcs.lcs.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.projectlcs.lcs.AndroidLuaEngine
import net.projectlcs.lcs.LCS
import net.projectlcs.lcs.LuaService
import net.projectlcs.lcs.Util.requireSdk
import java.time.LocalDateTime

/**
 * @param path generated uuid for given file
 * @param name name of script
 * @param createDate create date of script
 * @param lastModifyDate last modify date of script
 */
@Entity(tableName = "script_reference")
data class ScriptReference(
    @ColumnInfo("code") var code: String,
    @ColumnInfo("name") var name: String,
    @ColumnInfo("create_date") var createDate: LocalDateTime,
    @ColumnInfo("last_modify_date") var lastModifyDate: LocalDateTime,
    @ColumnInfo("is_paused") var isPaused: Boolean,
    @ColumnInfo("is_valid") var isValid: Boolean,
    @ColumnInfo("storage_access") var storageAccess: MutableList<String>,
    @ColumnInfo("error_strings") var errorString: String,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ScriptReference> {
            override fun createFromParcel(source: Parcel?) = source?.let {
                ScriptReference(
                    it.readString()!!,
                    it.readString()!!,
                    requireSdk(
                        Build.VERSION_CODES.TIRAMISU,
                        then = { it.readSerializable(null, LocalDateTime::class.java)!! },
                        not = { it.readSerializable() as LocalDateTime }),
                    requireSdk(
                        Build.VERSION_CODES.TIRAMISU,
                        then = { it.readSerializable(null, LocalDateTime::class.java)!! },
                        not = { it.readSerializable() as LocalDateTime }),
                    it.readBoolean(),
                    it.readBoolean(),
                    (0 until it.readInt()).map { _ -> it.readString()!! }.toMutableList(),
                    it.readString()!!,
                    it.readLong(),
                )
            }

            override fun newArray(size: Int) =
                (1..size).map { null }.toTypedArray<ScriptReference?>()
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(name)
        dest.writeSerializable(createDate)
        dest.writeSerializable(lastModifyDate)
        dest.writeBoolean(isPaused)
        dest.writeBoolean(isValid)
        dest.writeInt(storageAccess.size)
        storageAccess.forEach { dest.writeString(it) }
        dest.writeString(errorString)
        dest.writeLong(id)
    }
}

@Dao
interface ScriptReferenceDao {
    @Query("SELECT * FROM script_reference")
    fun getAllInstances(): Flow<List<ScriptReference>>

    @Query("SELECT * FROM script_reference WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Long): Flow<ScriptReference>

    @Insert
    fun insertAll(vararg references: ScriptReference): List<Long>

    @Delete
    fun delete(vararg reference: ScriptReference)

    @Update
    fun update(vararg references: ScriptReference)
}

@TypeConverters(LocalDateTimeConverter::class, MutableListConverter::class)
@Database(entities = [ScriptReference::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun scriptReferenceDao(): ScriptReferenceDao
}

object ScriptDataManager {
    private val db = Room.databaseBuilder(
        LCS.instance,
        ApplicationDatabase::class.java,
        "base.db"
    ).build()

    fun createNewScript(name: String): ScriptReference {
        val ref = ScriptReference(
            code = "",
            name = name,
            createDate = LocalDateTime.now(),
            lastModifyDate = LocalDateTime.now(),
            isPaused = false,
            storageAccess = mutableListOf(),
            isValid = false,
            errorString = ""
        )
        ref.id = db.scriptReferenceDao().insertAll(ref).first()
        return ref
    }

    /**
     * @param invalidateExisting true if immutable field changed and requires to restart task. setting this to false does not restart task
     */
    fun updateAllScript(vararg ref: ScriptReference, invalidateExisting: Boolean = true) {
        LuaService.INSTANCE?.let { service ->
            val engine = service.engine
            CoroutineScope(LuaService.INSTANCE!!.luaDispatcher).launch {
                LuaService.INSTANCE!!.engine.tasks.forEach {
                    val task = it as AndroidLuaEngine.AndroidLuaTask
                    ref.firstOrNull { it.id == task.ref.id }?.let {
                        if (invalidateExisting)
                            task.remove()
                        else {
                            task.isPaused = it.isPaused
                            it.lastModifyDate = LocalDateTime.now()
                        }
                    }
                }
                if (invalidateExisting)
                    ref.forEach {
                        val task = engine.createTask(
                            code = it.code,
                            name = it.name,
                            ref = it,
                            repeat = false,
                        )
                        task.isPaused = it.isPaused
                        it.isValid = task.isValid
                        it.lastModifyDate = LocalDateTime.now()
                    }
            }
        }

        db.scriptReferenceDao().update(*ref)
    }

    fun deleteAllScript(vararg ref: ScriptReference) {
        LuaService.INSTANCE?.let { svc ->
            CoroutineScope(svc.luaDispatcher).launch {
                svc.engine.tasks.removeIf { task -> ref.any { it.id == (task as AndroidLuaEngine.AndroidLuaTask).ref.id } }
            }
        }
        db.scriptReferenceDao().delete(*ref)
    }

    fun getAllScripts() = db.scriptReferenceDao().getAllInstances()
    fun getTaskById(id: Long) = db.scriptReferenceDao().getTaskById(id)
}