package net.projectlcs.lcs.data

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
import net.projectlcs.lcs.AndroidLuaEngine
import net.projectlcs.lcs.LCS
import net.projectlcs.lcs.LuaService
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
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
)

@Dao
interface ScriptReferenceDao {
    @Query("SELECT * FROM script_reference")
    fun getAllInstances(): List<ScriptReference>

    @Insert
    fun insertAll(vararg references: ScriptReference): List<Long>

    @Delete
    fun delete(vararg reference: ScriptReference)

    @Update
    fun update(vararg references: ScriptReference)
}

@TypeConverters(LocalDateTimeConverter::class)
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
            isValid = false
        )
        ref.id = db.scriptReferenceDao().insertAll(ref).first()
        return ref
    }

    fun updateAllScript(vararg ref: ScriptReference) {
        LuaService.INSTANCE?.let { svc ->
            svc.engine.tasks.forEach {
                val task = it as AndroidLuaEngine.AndroidLuaTask
                ref.firstOrNull { it == task.ref }?.let {
                    task.remove()
                    val task = task.engine.createTask(
                        code = it.code,
                        name = it.name,
                        repeat = true
                    )
                    task.isPaused = it.isPaused
                    it.isValid = task.isValid
                }
            }
        }

        db.scriptReferenceDao().update(*ref)
    }

    fun deleteAllScript(vararg ref: ScriptReference) {
        LuaService.INSTANCE?.let { svc ->
            svc.engine.tasks.removeIf { task -> ref.any { it == (task as AndroidLuaEngine.AndroidLuaTask).ref } }
        }
        db.scriptReferenceDao().delete(*ref)
    }

    fun getAllScripts() = db.scriptReferenceDao().getAllInstances()
}