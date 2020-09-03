package com.example.composeswishcards

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.*
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.expandVertically
import androidx.compose.*
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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.composeswishcards.ui.ComposeSwishCardsTheme
import com.example.composeswishcards.ui.shapes
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultPreview()
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    Column() {
        TopBar()
        DefaultFlashCard()
    }
}

@Composable
fun TopBar() {
    TopAppBar(
            title = {
                Text(text = "Flash Cards")
            },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.Menu)
                }
            })
}

@Composable
fun DefaultFlashCard() {

    val flashCards = FlashCards()

    Spacer(modifier = Modifier.height(30.dp))

    MaterialTheme {

        val typography = MaterialTheme.typography
        var question = remember { mutableStateOf(flashCards.currentFlashCards.question) }

        Column(modifier = Modifier.padding(30.dp).then(Modifier.fillMaxWidth())
                .then(Modifier.wrapContentSize(Alignment.Center))
                .clip(shape = RoundedCornerShape(16.dp))) {
            Box(modifier = Modifier.preferredSize(350.dp)
                    .border(width = 4.dp,
                            color = Gray,
                            shape = RoundedCornerShape(16.dp))
                    .clickable(
                            onClick = {
                                question.value = flashCards.currentFlashCards.answer })
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

            Text("Flash Card application",
                    style = typography.h6,
                    color = Black)

            Text("The following is a demonstration of using " +
                    "Android Compose to create a Flash Card",
                    style = typography.body2,
                    color = Black,
                    textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                flashCards.incrementQuestion();
                question.value = flashCards.currentFlashCards.question },
                    shape = RoundedCornerShape(10.dp),
                    content = { Text("Next Card") },
                    backgroundColor = Cyan)
        }
    }
}


data class Question(val question: String, val answer: String) {
}

class FlashCards() {

    val flashCards = listOf(
            Question("How many Bananas should go in a Smoothie?", "3 Bananas"),
            Question("How many Eggs does it take to make an Omellete?", "8 Eggs"),
            Question("How do you say Hello in Japenese?", "Konichiwa"),
            Question("What is Korea's currency?", "Won")
    )

    var currentQuestion = 0

    val currentFlashCards
        get() = flashCards[currentQuestion]

    fun incrementQuestion() {
        if (currentQuestion + 1 >= flashCards.size) currentQuestion = 0 else currentQuestion++
    }
}
