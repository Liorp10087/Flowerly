package com.example.flowerly.model

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

