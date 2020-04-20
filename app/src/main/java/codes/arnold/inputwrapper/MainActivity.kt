package codes.arnold.inputwrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import codes.arnold.inputwrapper.view.InputWrapper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val errorLength = findViewById<InputWrapper>(R.id.error_length)
        errorLength.setValidator { text -> text.length < 5 }
    }
}
