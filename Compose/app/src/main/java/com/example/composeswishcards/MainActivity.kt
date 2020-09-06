package com.example.composeswishcards

import androidx.compose.foundation.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.*
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: FlashCardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)

        setContent {
            Column(modifier = Modifier.fillMaxHeight()) {
                TopBar("Flash Cards") //set title
                DefaultFlashCard(viewModel)
            }
        }

    }
}

@Composable
fun TopBar(text: String) {
    TopAppBar(
        title = {
            Text(text = text)
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Menu)
            }
        })
}

@Composable
fun DefaultFlashCard(model: ViewModel, text: List<String>) {

    val viewModel = model as FlashCardViewModel
    val title = "Flash Card Application"

    Spacer(modifier = Modifier.height(30.dp))
    MaterialTheme {

        val typography = MaterialTheme.typography
        var question = remember { mutableStateOf(viewModel.flashCards.currentFlashCards) }

        Column(modifier = Modifier.padding(30.dp).then(Modifier.fillMaxWidth())
                .then(Modifier.wrapContentSize(Alignment.Center))
                .clip(shape = RoundedCornerShape(16.dp))) {

            Box(modifier = Modifier.preferredSize(350.dp)
                    .border(width = 4.dp,
                            color = Gray,
                            shape = RoundedCornerShape(16.dp))
                    .clickable(
                            onClick = {
                                viewModel.flashCards.changeState(State.answer);
                                question.value = viewModel.flashCards.currentFlashCards})
                    .gravity(align = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(2.dp),
                    backgroundColor = DarkGray,
                    gravity = Alignment.Center) {
                Text("${question.value}",
                        style = typography.h4, textAlign = TextAlign.Center, color = White
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp),
                horizontalGravity = Alignment.CenterHorizontally) {

            Text(title,
                    style = typography.h6,
                    color = Black)

            for (t in text) {
            Text(t,
                    style = typography.body2,
                    color = Black,
                    textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                viewModel.flashCards.incrementQuestion(); question.value = viewModel.flashCards.currentFlashCards},
                    shape = RoundedCornerShape(10.dp),
                    content = { Text("Next Card") },
                    backgroundColor = Cyan)
        }
    }
}

data class Question(val question: String, val answer: String) {
}

enum class State {
    question,
    answer
}

class FlashCards(cards: List<Question>) {

    val flashCards = cards

    var currentQuestion = 0

    val currentFlashCards
        get() = if (currentState == State.question) flashCards[currentQuestion].question else flashCards[currentQuestion].answer

    var currentState: State = State.question

    val changeState = {state: State -> currentState = state }

    fun incrementQuestion() {
        if (currentQuestion + 1 >= flashCards.size) {
            currentQuestion = 0
        } else {
            currentQuestion++
        }
        currentState = State.question
    }
}

class FlashCardViewModel: ViewModel() {

    var flashCards = (FlashCards( listOf(
            Question("How many Bananas should go in a Smoothie?", "3 Bananas"),
            Question("How many Eggs does it take to make an Omellete?", "8 Eggs"),
            Question("How do you say Hello in Japenese?", "Konichiwa"),
            Question("What is Korea's currency?", "Won")
    )))
}
