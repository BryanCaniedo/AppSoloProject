package com.example.myapplication

import android.graphics.Color
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONException
import kotlin.random.Random

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.TreeSet

class MainActivity : AppCompatActivity() {
    private lateinit var  timer: CountDownTimer
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var textInput: EditText
    private val numberOfWordsEng: Int = 2286
    private val numberOfWordsFr: Int = 30000
    private var countdownStartTime: Long = 0
    private val countdownDuration: Long = 120_000
    private var isFrenchWord: Boolean = false
    private var isCountdownTimerStarted: Boolean = false
    private lateinit var englishWordsSet: Set<String>
    private lateinit var frenchWordsSet: Set<String>
    private lateinit var inputButton: Button
    private lateinit var userTextInput: TextView
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var  newRecyclerView: RecyclerView
    private lateinit var  newArrayList: ArrayList<dataWord>
    lateinit var word : Array<String>
    private var selectedLanguage: String = ""
    private var isButtonPressed: Boolean = false
    private var maxLengthAllowed: Int = 4
    private var generatedWord: String = ""
    private var threeLetterWord: String = ""
    private var isFirstSubmission: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        loadWordsSets()

        word = arrayOf(

        )
        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<dataWord>()
        getUserdata()

        textInput = findViewById(R.id.textInput)
        inputButton = findViewById(R.id.inputButton)
        timerTextView = findViewById(R.id.timerTextView)

        inputButton.setOnClickListener {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

            navHostFragment?.view?.visibility = View.GONE
            val userInput = textInput.text.toString().trim().toLowerCase()
            generatedWord = getGeneratedWord()

            if (!getIsButtonPressed()) {
                showToast("Please select a language first.")
                return@setOnClickListener
            }

            if (isCountdownTimerStarted && countdownStartTime > 0) {
                val elapsedTime = System.currentTimeMillis() - countdownStartTime
                if (elapsedTime >= countdownDuration) {
                    showToast("Countdown timer has finished. You cannot add more words.")
                    return@setOnClickListener
                }
            }

            if (userInput.length != maxLengthAllowed && userInput.length != maxLengthAllowed + 1) {
                showToast("Please enter a word of length $maxLengthAllowed or ${maxLengthAllowed + 1}.")
                return@setOnClickListener
            }
            val userLettersFromGeneratedWord = userInput.filter { it in generatedWord }
            if (userLettersFromGeneratedWord.length != userInput.length) {
                showToast("Please use only letters from the generated word: $generatedWord")
                return@setOnClickListener
            }
            if (getIsFrenchWord()) {
                val filteredWords = frenchWordsSet.filter { it.equals(userInput, ignoreCase = true) }
                if (filteredWords.isNotEmpty()) {
                    if (addWordIfCorrect(userInput)) {
                        if (userInput.length >= generatedWord.length) {
                            countDownTimer.cancel()
                            timerTextView.text = "00:00"
                            showToast("Game over! You have guessed the word.")
                        } else {
                            maxLengthAllowed++
                            if (!isCountdownTimerStarted) {
                                countDownTimer.start()
                                isCountdownTimerStarted = true
                                countdownStartTime = System.currentTimeMillis()
                            }
                        }
                    }
                } else {
                    showToast("Please enter a valid French word.")
                }
            } else {

                val filteredWords = englishWordsSet.filter { it.equals(userInput, ignoreCase = true) }
                if (filteredWords.isNotEmpty()) {
                    if (addWordIfCorrect(userInput)) {
                        if (userInput.length >= generatedWord.length) {
                            showToast("Game over! You have guessed the word. ${userInput.length} space ${generatedWord.length}")
                            // Stop the countdown timer and display "00:00"
                            countDownTimer.cancel()
                            timerTextView.text = "00:00"
                            showToast("Game over! You have guessed the word.")
                        } else {
                            maxLengthAllowed++
                            if (!isCountdownTimerStarted) {
                                countDownTimer.start()
                                isCountdownTimerStarted = true
                                countdownStartTime = System.currentTimeMillis()
                            }
                        }
                    }
                } else {
                    showToast("Please enter a valid English word.")
                }
            }
            setIsFirstSubmission(false)
        }

        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                if (millisUntilFinished <= 10000) {
                    timerTextView.setTextColor(0xFFFFF192.toInt())
                } else {
                    timerTextView.setTextColor(0xFFA7D489.toInt())
                }
                if (millisUntilFinished <= 0) {
                    timerTextView.setTextColor(0xFFFF6961.toInt())
                    timerTextView.text = "00:00"
                    cancel()
                } else {
                    timerTextView.text = timeString
                }
            }
            override fun onFinish() {
                timerTextView.setTextColor(Color.RED)
                timerTextView.text = "00:00"
            }
        }
    }

    private fun addWordIfCorrect(userInput: String): Boolean {
        val isCorrectWord: Boolean
        if (getIsFrenchWord()) {
            val filteredWords = frenchWordsSet.filter { it == userInput }
            isCorrectWord = filteredWords.isNotEmpty()
        } else {
            val filteredWords = englishWordsSet.filter { it == userInput }
            isCorrectWord = filteredWords.isNotEmpty()
        }

        if (isCorrectWord) {
            val dataWordObject = dataWord(userInput)
            newArrayList.add(dataWordObject)
            newRecyclerView.adapter?.notifyItemInserted(newArrayList.size - 1)
            newRecyclerView.scrollToPosition(newArrayList.size - 1)
            textInput.text.clear()
        } else {
            showToast("Please enter a valid word.")
        }
        return isCorrectWord
    }

    private fun getUserdata() {
        for (i in word.indices){
            val someDataWord = dataWord(word[i])
            newArrayList.add(someDataWord)
        }
        newRecyclerView.adapter = MyAdapter(newArrayList)
    }


    private fun loadWordsSets() {
        englishWordsSet = saveAllWords(loadJsonFromAssets("indexEng.json"))
        frenchWordsSet = saveAllWords(loadJsonFromAssets("indexFr.json"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    fun generateRandomEnglishWord(){
        isFrenchWord = false // Reset the flag
        val inputStream: InputStream = assets.open("indexEng.json")
        val json: String = inputStream.bufferedReader().use { it.readText() }
        val filteredWords = filterWords(json)
        if (filteredWords.isNotEmpty()) {
            val randomEnglishWord = filteredWords.random()
            var jsonText = findViewById<TextView>(R.id.json_text)
            var threeLetterWord = findViewById<TextView>(R.id.tester_2_text)
            jsonText.text = randomEnglishWord
            threeLetterWord.text = threeLetterWord(randomEnglishWord)
            setGeneratedWord(randomEnglishWord)
        } else {
        }
    }

    fun generateRandomFrenchWord() {
        isFrenchWord = true // Set the flag
        val inputStream: InputStream = assets.open("indexFr.json")
        val json: String = inputStream.bufferedReader().use { it.readText() }
        val filteredWords = filterWords(json)
        if (filteredWords.isNotEmpty()) {
            val randomFrenchWord = filteredWords.random()
            var jsonText = findViewById<TextView>(R.id.json_text)
            var threeLetterWord = findViewById<TextView>(R.id.tester_2_text)
            jsonText.text = randomFrenchWord
            threeLetterWord.text = threeLetterWord(randomFrenchWord)
            setGeneratedWord(randomFrenchWord)
        } else {
        }
    }

    private fun filterWords(jsonString: String): List<String> {
        val wordsList = mutableListOf<String>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val word = jsonArray.getString(i)
                if (word.length > 3) {
                    wordsList.add(word)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return wordsList
    }


    private fun loadJsonFromAssets(fileName: String): String {
        return try {
            val inputStream: InputStream = assets.open(fileName)
            inputStream.bufferedReader().useLines { lines ->
                lines.joinToString(separator = "")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    fun isLastWordFrench(): Boolean {
        return isFrenchWord
    }

    private fun saveAllWords(jsonString: String): Set<String> {
        val wordsSet = TreeSet<String>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val word = jsonArray.getString(i)
                wordsSet.add(word)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return wordsSet
    }

    fun threeLetterWord(word: String): String {
        val letters = word.toList()
        val shuffledLetters = letters.shuffled()
        val randomLetters = shuffledLetters.take(3)
        val threeLetters = randomLetters.joinToString("")
        setThreeLetterWord(threeLetters)
        return threeLetters
    }


    fun toastTest(wordTest: String) {
        Toast.makeText(this, wordTest, Toast.LENGTH_SHORT).show()
    }

    fun saveRandomWord(jsonString: String, randomJsonWordNumber: Int): String? {
        var words = jsonString.split(",")
        return if (words.size >= 3) {
            val thirdWord = words[randomJsonWordNumber].trim().replace("\"", "")
            thirdWord
        } else {
            null
        }
    }

    fun clearRecyclerViewData() {
        newArrayList.clear()
        newRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun setIsButtonPressed(value: Boolean) {
        isButtonPressed = value
    }

    fun getIsButtonPressed(): Boolean {
        return isButtonPressed
    }

    fun setIsFrenchWord(value: Boolean) {
        isFrenchWord = value
    }

    fun getIsFrenchWord(): Boolean {
        return isFrenchWord
    }

    fun setGeneratedWord(word: String) {
        generatedWord = word
    }

    fun getGeneratedWord(): String {
        return generatedWord
    }

    fun getThreeLetterWord(): String {
        return threeLetterWord
    }

    fun setThreeLetterWord(word: String) {
        threeLetterWord = word
    }

    fun setIsFirstSubmission(value: Boolean) {
        isFirstSubmission = value
    }
    fun getIsFirstSubmission(): Boolean {
        return isFirstSubmission
    }


}