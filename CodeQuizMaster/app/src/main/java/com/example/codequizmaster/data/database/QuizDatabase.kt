package com.example.codequizmaster.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codequizmaster.data.dao.GameSessionDao
import com.example.codequizmaster.data.dao.QuestionDao
import com.example.codequizmaster.data.dao.UserDao
import com.example.codequizmaster.data.entity.GameSession
import com.example.codequizmaster.data.entity.Question
import com.example.codequizmaster.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Question::class, User::class, GameSession::class],
    version = 1,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun userDao(): UserDao
    abstract fun gameSessionDao(): GameSessionDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.questionDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(questionDao: QuestionDao) {
            // EASY otázky
            val easyQuestions = listOf(
                Question(
                    questionText = "Jaký je správný způsob deklarace proměnné v Kotlinu?",
                    correctAnswer = "val x = 10",
                    wrongAnswer1 = "int x = 10",
                    wrongAnswer2 = "var x: 10",
                    wrongAnswer3 = "let x = 10",
                    difficulty = "EASY",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co znamená 'val' v Kotlinu?",
                    correctAnswer = "Proměnná pouze pro čtení",
                    wrongAnswer1 = "Proměnná pro zápis",
                    wrongAnswer2 = "Konstanta",
                    wrongAnswer3 = "Funkce",
                    difficulty = "EASY",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Která metoda se volá při vytvoření Activity?",
                    correctAnswer = "onCreate()",
                    wrongAnswer1 = "onStart()",
                    wrongAnswer2 = "onResume()",
                    wrongAnswer3 = "onInit()",
                    difficulty = "EASY",
                    category = "Android"
                ),
                Question(
                    questionText = "Co je to null v programování?",
                    correctAnswer = "Absence hodnoty",
                    wrongAnswer1 = "Nulová hodnota",
                    wrongAnswer2 = "Prázdný string",
                    wrongAnswer3 = "False",
                    difficulty = "EASY",
                    category = "OOP"
                ),
                Question(
                    questionText = "Jaký soubor obsahuje layout v Androidu?",
                    correctAnswer = "XML soubor",
                    wrongAnswer1 = "Java soubor",
                    wrongAnswer2 = "Kotlin soubor",
                    wrongAnswer3 = "JSON soubor",
                    difficulty = "EASY",
                    category = "Android"
                ),
                Question(
                    questionText = "Co znamená OOP?",
                    correctAnswer = "Object Oriented Programming",
                    wrongAnswer1 = "Open Object Protocol",
                    wrongAnswer2 = "Operating Object Platform",
                    wrongAnswer3 = "Ordered Operation Process",
                    difficulty = "EASY",
                    category = "OOP"
                ),
                Question(
                    questionText = "Jak se nazývá hlavní funkce v Kotlin programu?",
                    correctAnswer = "main()",
                    wrongAnswer1 = "start()",
                    wrongAnswer2 = "begin()",
                    wrongAnswer3 = "init()",
                    difficulty = "EASY",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je ViewGroup v Androidu?",
                    correctAnswer = "Kontejner pro View elementy",
                    wrongAnswer1 = "Skupina uživatelů",
                    wrongAnswer2 = "Databázová tabulka",
                    wrongAnswer3 = "Typ proměnné",
                    difficulty = "EASY",
                    category = "Android"
                ),
                Question(
                    questionText = "Jaký keyword se používá pro dědičnost v Kotlinu?",
                    correctAnswer = ":",
                    wrongAnswer1 = "extends",
                    wrongAnswer2 = "implements",
                    wrongAnswer3 = "inherit",
                    difficulty = "EASY",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je to IDE?",
                    correctAnswer = "Integrated Development Environment",
                    wrongAnswer1 = "Internet Data Exchange",
                    wrongAnswer2 = "Interactive Design Editor",
                    wrongAnswer3 = "Internal Debug Engine",
                    difficulty = "EASY",
                    category = "General"
                )
            )

            // MEDIUM otázky
            val mediumQuestions = listOf(
                Question(
                    questionText = "Co je to coroutine v Kotlinu?",
                    correctAnswer = "Lehký thread pro asynchronní programování",
                    wrongAnswer1 = "Typ databáze",
                    wrongAnswer2 = "Grafická komponenta",
                    wrongAnswer3 = "Design pattern",
                    difficulty = "MEDIUM",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "K čemu slouží LiveData v Androidu?",
                    correctAnswer = "Pozorovatelný držitel dat",
                    wrongAnswer1 = "Databázový driver",
                    wrongAnswer2 = "Animační knihovna",
                    wrongAnswer3 = "Síťový protokol",
                    difficulty = "MEDIUM",
                    category = "Android"
                ),
                Question(
                    questionText = "Co je to Room Database?",
                    correctAnswer = "Abstraktní vrstva nad SQLite",
                    wrongAnswer1 = "Cloudová databáze",
                    wrongAnswer2 = "In-memory databáze",
                    wrongAnswer3 = "Grafová databáze",
                    difficulty = "MEDIUM",
                    category = "Android"
                ),
                Question(
                    questionText = "Co dělá keyword 'suspend' v Kotlinu?",
                    correctAnswer = "Označuje funkci, která může být pozastavena",
                    wrongAnswer1 = "Pozastaví celou aplikaci",
                    wrongAnswer2 = "Vytvoří nový thread",
                    wrongAnswer3 = "Zablokuje UI",
                    difficulty = "MEDIUM",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je MVVM pattern?",
                    correctAnswer = "Model-View-ViewModel architektura",
                    wrongAnswer1 = "Multi-Version View Manager",
                    wrongAnswer2 = "Mobile Virtual View Mode",
                    wrongAnswer3 = "Main View Visibility Model",
                    difficulty = "MEDIUM",
                    category = "Architecture"
                ),
                Question(
                    questionText = "K čemu slouží ViewModel v Androidu?",
                    correctAnswer = "Uchovává data při změně konfigurace",
                    wrongAnswer1 = "Renderuje views",
                    wrongAnswer2 = "Spravuje databázi",
                    wrongAnswer3 = "Obsluhuje síťové požadavky",
                    difficulty = "MEDIUM",
                    category = "Android"
                ),
                Question(
                    questionText = "Co je to lambda výraz?",
                    correctAnswer = "Anonymní funkce",
                    wrongAnswer1 = "Proměnná",
                    wrongAnswer2 = "Cyklus",
                    wrongAnswer3 = "Podmínka",
                    difficulty = "MEDIUM",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co znamená '?' za typem v Kotlinu?",
                    correctAnswer = "Typ může být nullable",
                    wrongAnswer1 = "Nepovinný parametr",
                    wrongAnswer2 = "Otazník v názvu",
                    wrongAnswer3 = "Ternární operátor",
                    difficulty = "MEDIUM",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je to RecyclerView?",
                    correctAnswer = "Efektivní způsob zobrazení seznamů",
                    wrongAnswer1 = "Databázový view",
                    wrongAnswer2 = "Animační framework",
                    wrongAnswer3 = "Návrhový pattern",
                    difficulty = "MEDIUM",
                    category = "Android"
                ),
                Question(
                    questionText = "K čemu slouží Intent v Androidu?",
                    correctAnswer = "Komunikace mezi komponentami",
                    wrongAnswer1 = "Ukládání dat",
                    wrongAnswer2 = "Renderování UI",
                    wrongAnswer3 = "Správa paměti",
                    difficulty = "MEDIUM",
                    category = "Android"
                )
            )

            // HARD otázky
            val hardQuestions = listOf(
                Question(
                    questionText = "Co je to Flow v Kotlinu?",
                    correctAnswer = "Asynchronní stream dat",
                    wrongAnswer1 = "Control flow struktura",
                    wrongAnswer2 = "Layout manager",
                    wrongAnswer3 = "Animační knihovna",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co dělá 'reified' keyword v Kotlinu?",
                    correctAnswer = "Umožňuje přístup k typu v runtime",
                    wrongAnswer1 = "Vytváří singleton",
                    wrongAnswer2 = "Optimalizuje kód",
                    wrongAnswer3 = "Zabraňuje dědičnosti",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je to StateFlow?",
                    correctAnswer = "Hot Flow s aktuálním stavem",
                    wrongAnswer1 = "Database state manager",
                    wrongAnswer2 = "UI state container",
                    wrongAnswer3 = "Navigation component",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Jaký je rozdíl mezi 'by lazy' a 'lateinit'?",
                    correctAnswer = "lazy je delegovaná inicializace, lateinit pro var",
                    wrongAnswer1 = "lazy je rychlejší",
                    wrongAnswer2 = "lateinit je bezpečnější",
                    wrongAnswer3 = "Žádný rozdíl",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je to Dependency Injection?",
                    correctAnswer = "Návrhový pattern pro správu závislostí",
                    wrongAnswer1 = "Databázová technika",
                    wrongAnswer2 = "Testovací framework",
                    wrongAnswer3 = "Síťový protokol",
                    difficulty = "HARD",
                    category = "Architecture"
                ),
                Question(
                    questionText = "K čemu slouží @Composable anotace?",
                    correctAnswer = "Označuje funkci pro Jetpack Compose",
                    wrongAnswer1 = "Vytváří databázovou entitu",
                    wrongAnswer2 = "Definuje coroutine",
                    wrongAnswer3 = "Nastavuje lifecycle",
                    difficulty = "HARD",
                    category = "Android"
                ),
                Question(
                    questionText = "Co je to backing property pattern?",
                    correctAnswer = "Privátní mutable, public immutable property",
                    wrongAnswer1 = "Databázová indexace",
                    wrongAnswer2 = "UI pattern",
                    wrongAnswer3 = "Threading pattern",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co dělá 'inline' keyword u funkce?",
                    correctAnswer = "Vkládá kód funkce přímo na místo volání",
                    wrongAnswer1 = "Vytváří inline class",
                    wrongAnswer2 = "Zabraňuje dědičnosti",
                    wrongAnswer3 = "Optimalizuje paměť",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "Co je to sealed class?",
                    correctAnswer = "Omezená hierarchie tříd",
                    wrongAnswer1 = "Finální třída",
                    wrongAnswer2 = "Abstraktní třída",
                    wrongAnswer3 = "Singleton třída",
                    difficulty = "HARD",
                    category = "Kotlin"
                ),
                Question(
                    questionText = "K čemu slouží WorkManager?",
                    correctAnswer = "Plánování a spouštění deferovaných úloh",
                    wrongAnswer1 = "Správa vláken",
                    wrongAnswer2 = "Database management",
                    wrongAnswer3 = "UI rendering",
                    difficulty = "HARD",
                    category = "Android"
                )
            )

            questionDao.insertAll(easyQuestions + mediumQuestions + hardQuestions)
        }
    }
}