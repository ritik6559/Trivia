package com.example.trivia.component


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.trivia.model.QuestionItem
import com.example.trivia.screens.QuestionViewModel
import com.example.trivia.util.AppColors


@Composable
fun Questions(viewModel: QuestionViewModel) {

    val questions = viewModel.data.value.data?.toMutableList()//converting array list to mutable list, as composable needs a mutable list.

    val questionIndex = remember {
        mutableStateOf(0)
    }
    if (viewModel.data.value.loading == true){
        CircularProgressIndicator(
            modifier = Modifier.size(1.dp)
        )
    }else{
        val question = try{
            questions?.get(questionIndex.value)
        }catch (ex:Exception){
            null
        }
        if (questions != null) {
            QuestionDisplay(question = question!!,
                questionIndex = questionIndex,
                viewModel = viewModel,
                onNextClicked = {
                    questionIndex.value += 1
                },
                onPrevClicked = {
                    questionIndex.value -= 1
                })
        }

    }
}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionViewModel,
    onNextClicked: (Int)->Unit = {},
    onPrevClicked: (Int)->Unit = {}
){

    val choicesState = remember(question){
        question.choices.toMutableList()
    }

    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }

    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)

    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer//searching for correct answer among the options.

        }

    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f),0f)//it means 10px off 10px on.
    Surface(modifier = Modifier
        .fillMaxWidth()
        .fillMaxWidth(),
        color = AppColors.mDarkPurple){
        Column(modifier = Modifier
            .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            if (questionIndex.value >= 3) ShowProgress(score = questionIndex.value)

            QuestionTracker(counter = questionIndex.value,viewModel.getTotalQuestionCount())
            DrawDottedLine(pathEffect)

            Column {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.5f),//0.3 fraction or 30% of the entire screen
                    fontSize = 17.sp,
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                //choices
                choicesState.forEachIndexed { index, answerText ->
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .border(
                                width = 4.dp,
                                shape = RoundedCornerShape(15.dp),
                                brush = Brush.linearGradient(
                                    colors = listOf(AppColors.mDarkPurple, AppColors.mOffDarkPurple)
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50,
                                    bottomStartPercent = 50,
                                    bottomEndPercent = 50
                                )
                            )
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index),
                            onClick = {
                                updateAnswer(index)
                            },
                            modifier = Modifier.padding(start = 6.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (correctAnswerState.value == true
                                    && index == answerState.value
                                ) {
                                    Color.Green.copy(alpha = 0.2f)//used to dim color
                                } else {
                                    Color.Red.copy(alpha = 0.2f)
                                }
                            )
                        )//end rb

                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Light,
                                    color = if (correctAnswerState.value == true
                                        && index == answerState.value
                                    ) {
                                        Color.Green
                                    } else if (correctAnswerState.value != true && index == answerState.value) {
                                        Color.Red
                                    } else {
                                        AppColors.mOffWhite
                                    },
                                    fontSize = 17.sp
                                )
                            ) {
                                append(answerText)
                            }
                        }
                        Text(
                            text = annotatedString,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
                Row(modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)) {
                    if (questionIndex.value > 0 ) {
                        Button(
                            onClick = { onPrevClicked(questionIndex.value) },
                            modifier = Modifier
                                .padding(3.dp),

                            shape = RoundedCornerShape(34.dp),
                            colors = buttonColors(AppColors.mLightBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Icons"
                            )
                        }
                        Spacer(modifier = Modifier.padding(105.dp))
                    }

                    Button(
                        onClick = { onNextClicked(questionIndex.value) },
                        modifier = Modifier
                            .padding(3.dp),
                        shape = RoundedCornerShape(34.dp),
                        colors = buttonColors(AppColors.mLightBlue)
                        ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = "Icons"
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun DrawDottedLine(pathEffect: PathEffect){
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp),
        ) {
        drawLine(color = AppColors.mLightGray,
            start = Offset(0f,0f),//starting point from corner of the screen.
            end = Offset(size.width,0f),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun ShowProgress(score:Int){

    val gradient = Brush.linearGradient(listOf(Color(0xFFF95075),
        Color(0xFFBE6BE5)))

    val progressFactor by remember(score){//when we use "by" we do not have to .value to get value.
        mutableStateOf(score*0.005f)
    }
    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(35.dp)
        .clip(
            RoundedCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomEndPercent = 50,
                bottomStartPercent = 50
            )
        )
        .background(Color.Transparent)
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    AppColors.mLightPurple, AppColors.mLightPurple
                )
            ),
            shape = RoundedCornerShape(34.dp)
        )){
        Button(onClick = {},
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactor)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            Text(text = (score).toString(),
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxWidth(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center)
        }
        
    }
}

@Composable
fun QuestionTracker( counter: Int,
                     outOff: Int) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)){
            withStyle(style = SpanStyle(color = AppColors.mLightGray,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp)){
                append("Question ${counter+1}/")
                withStyle(style = SpanStyle(color = AppColors.mLightGray,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp)){
                    append("$outOff")
                }
            }
        }
    },
        modifier = Modifier
            .padding(20.dp))
}