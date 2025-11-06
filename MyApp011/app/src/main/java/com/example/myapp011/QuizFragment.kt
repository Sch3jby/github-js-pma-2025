package com.example.myapp011

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp011.databinding.FragmentQuizBinding

// Data třída pro otázku
data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int // index správné odpovědi (0-3)
)

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var topic: String = ""
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var questions: List<Question>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Získat téma z argumentů
        topic = arguments?.getString("topic") ?: "Sport"

        // Načíst otázky podle tématu
        questions = getQuestionsForTopic(topic)

        // Nastavit barvu tématu
        binding.tvTopic.text = topic
        binding.tvTopic.setTextColor(getTopicColor(topic))

        // Zobrazit první otázku
        showQuestion()

        // Tlačítko Další
        binding.btnNext.setOnClickListener {
            checkAnswer()
        }
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]

        binding.tvProgress.text = "Otázka ${currentQuestionIndex + 1}/${questions.size}"
        binding.tvQuestion.text = question.text

        binding.rbOption1.text = question.options[0]
        binding.rbOption2.text = question.options[1]
        binding.rbOption3.text = question.options[2]
        binding.rbOption4.text = question.options[3]

        // Vymazat výběr
        binding.radioGroup.clearCheck()
        binding.btnNext.text = "Potvrdit odpověď"
    }

    private fun checkAnswer() {
        val selectedId = binding.radioGroup.checkedRadioButtonId

        if (selectedId == -1) {
            Toast.makeText(context, "Vyber odpověď!", Toast.LENGTH_SHORT).show()
            return
        }

        // Zjistit index vybrané odpovědi
        val selectedIndex = when (selectedId) {
            R.id.rbOption1 -> 0
            R.id.rbOption2 -> 1
            R.id.rbOption3 -> 2
            R.id.rbOption4 -> 3
            else -> -1
        }

        val question = questions[currentQuestionIndex]

        if (selectedIndex == question.correctAnswer) {
            score++
            // Custom Toast se zeleným pozadím
            val toast = Toast.makeText(context, "✅ Správně!", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            Toast.makeText(context, "❌ Špatně! Správná odpověď: ${question.options[question.correctAnswer]}", Toast.LENGTH_LONG).show()
        }

        // Přejít na další otázku nebo výsledek
        currentQuestionIndex++

        if (currentQuestionIndex < questions.size) {
            showQuestion()
        } else {
            // Uložit skóre do SharedPreferences
            saveScore()

            // Přejít na výsledek
            (activity as MainActivity).navigateToResult(score, questions.size, topic)
        }
    }

    private fun saveScore() {
        val prefs = requireContext().getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Celkové skóre
        val totalScore = prefs.getInt("total_score", 0)
        editor.putInt("total_score", totalScore + score)

        // Skóre pro konkrétní téma
        val topicKey = topic.lowercase()
        val topicScore = prefs.getInt("${topicKey}_score", 0)
        val topicTotal = prefs.getInt("${topicKey}_total", 0)

        editor.putInt("${topicKey}_score", topicScore + score)
        editor.putInt("${topicKey}_total", topicTotal + questions.size)

        // Počet hraných kvízů
        val quizzesPlayed = prefs.getInt("quizzes_played", 0)
        editor.putInt("quizzes_played", quizzesPlayed + 1)

        editor.apply()
    }

    private fun getQuestionsForTopic(topic: String): List<Question> {
        return when (topic) {
            "Sport" -> listOf(
                Question("Kde se konaly olympijské hry v roce 2020?",
                    listOf("Tokio", "Peking", "Londýn", "Rio de Janeiro"), 0),
                Question("Který sport hraje Roger Federer?",
                    listOf("Tenis", "Golf", "Fotbal", "Hokej"), 0),
                Question("Kolik hráčů je v basketbalovém týmu na hřišti?",
                    listOf("5", "6", "7", "11"), 0),
                Question("Jaká je délka maratonu?",
                    listOf("42,195 km", "40 km", "50 km", "35 km"), 0),
                Question("V jakém sportu se používá puk?",
                    listOf("Hokej", "Baseball", "Kriket", "Ragby"), 0)
            )
            "Historie" -> listOf(
                Question("V jakém roce skončila 2. světová válka?",
                    listOf("1945", "1944", "1946", "1943"), 0),
                Question("Kdo byl první prezident USA?",
                    listOf("George Washington", "Abraham Lincoln", "Thomas Jefferson", "John Adams"), 0),
                Question("Ve kterém století žil Leonardo da Vinci?",
                    listOf("15.-16. století", "14.-15. století", "16.-17. století", "13.-14. století"), 0),
                Question("Která civilizace postavila Machu Picchu?",
                    listOf("Inkové", "Aztékové", "Mayové", "Egypťané"), 0),
                Question("V jakém roce spadl Berlínská zeď?",
                    listOf("1989", "1991", "1985", "1990"), 0)
            )
            "Věda" -> listOf(
                Question("Jaký je chemický vzorec vody?",
                    listOf("H2O", "CO2", "O2", "H2SO4"), 0),
                Question("Kolik planet je ve sluneční soustavě?",
                    listOf("8", "9", "7", "10"), 0),
                Question("Kdo objevil gravitaci?",
                    listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Stephen Hawking"), 0),
                Question("Jaká je rychlost světla?",
                    listOf("300 000 km/s", "150 000 km/s", "500 000 km/s", "100 000 km/s"), 0),
                Question("Který orgán v těle filtruje krev?",
                    listOf("Ledviny", "Srdce", "Plíce", "Játra"), 0)
            )
            "Filmy" -> listOf(
                Question("Kdo režíroval film Titanic?",
                    listOf("James Cameron", "Steven Spielberg", "Christopher Nolan", "Martin Scorsese"), 0),
                Question("V jakém roce vyšel první film Star Wars?",
                    listOf("1977", "1980", "1975", "1983"), 0),
                Question("Který film získal nejvíce Oscarů (11)?",
                    listOf("Titanic", "Avatar", "Pán prstenů", "Ben-Hur"), 0),
                Question("Kdo hrál hlavní roli v filmu Forrest Gump?",
                    listOf("Tom Hanks", "Brad Pitt", "Leonardo DiCaprio", "Matt Damon"), 0),
                Question("Jaký je název prvního plnometrážního filmu Pixar?",
                    listOf("Toy Story", "Hledá se Nemo", "WALL-E", "Auta"), 0)
            )
            else -> emptyList()
        }
    }

    private fun getTopicColor(topic: String): Int {
        return when (topic) {
            "Sport" -> Color.parseColor("#FF9800")
            "Historie" -> Color.parseColor("#9C27B0")
            "Věda" -> Color.parseColor("#00BCD4")
            "Filmy" -> Color.parseColor("#E91E63")
            else -> Color.BLACK
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}