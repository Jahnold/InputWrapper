package codes.arnold.inputwrapper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import codes.arnold.inputwrapper.view.InputWrapper
import codes.arnold.inputwrapper.view.behaviours.TextCaseBehaviour

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val errorLength = findViewById<InputWrapper>(R.id.error_length)
        errorLength.setValidator { text -> text.length > 5 || text.isEmpty() }

        val customBehaviour = findViewById<InputWrapper>(R.id.custom_behaviour)
        customBehaviour.setBehaviour(TextCaseBehaviour(customBehaviour))
    }
}
