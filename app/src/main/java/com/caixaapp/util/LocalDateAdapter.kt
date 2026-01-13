package com.caixaapp.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter, value: LocalDate?) {
        out.value(value?.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun read(input: JsonReader): LocalDate? {
        return if (input.peek() == null) {
            null
        } else {
            LocalDate.parse(input.nextString(), DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }
}
