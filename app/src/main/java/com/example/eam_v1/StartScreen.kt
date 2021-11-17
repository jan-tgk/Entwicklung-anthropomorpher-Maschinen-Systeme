package com.example.eam_v1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.Animate
import com.aldebaran.qi.sdk.`object`.conversation.BookmarkStatus
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.holder.AutonomousAbilitiesType
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy


private const val TAG = "StartScreen"

class StartScreen : RobotActivity(), RobotLifecycleCallbacks {
    //private lateinit var chat: Chat
    private var qiContext: QiContext? =null
    //Store the greet BookmarkStatus
    private lateinit var MainActivityBookmarkStatus: BookmarkStatus


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"on Create")
        super.onCreate(savedInstanceState)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setContentView(R.layout.activity_start_screen)
        QiSDK.register(this,this)
    }

    private fun holdAbilities(qiContext: QiContext){
        //Build and store the holder for the abilities.
        val holder: Holder = HolderBuilder.with(qiContext)
            .withAutonomousAbilities(
                AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                AutonomousAbilitiesType.BASIC_AWARENESS,
                AutonomousAbilitiesType.AUTONOMOUS_BLINKING
            ).build()
        // Hold the abilities asynchronously.
        holder.async().hold()
        Log.i(TAG,"hold Abilities")
    }


    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext
        //Autonome Abilites starten
        holdAbilities(qiContext)

        //Begrüßung des Benutzers + Animation
        val greeting = makeSay(" ")
        val animateGreeting = makeAnimate(R.raw.hello_a010)
        Future.waitAll(greeting.async().run(),
            animateGreeting.async().run()).value
        Log.i(TAG,"Greeting done")

        //Festlegen der Sprache des Roboters//
        val locale = com.aldebaran.qi.sdk.`object`.locale.Locale(Language.GERMAN, Region.GERMANY)
        //Einbinden des zuvor erstellten Topic
        val topic = TopicBuilder.with(qiContext).withResource(R.raw.start_screen).build()
        // Erstellen eines qiChatbos mittels Chatbotbilder und unter Einbeziehung der Spracheinstellungen
        val qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).withLocale(locale).build()
        //Chat erstellen
        val chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).withLocale(locale).build()


        // bookmarks stuff
        val bookmarks = topic.bookmarks

        //Get  bookmark
        val MainActivityBookmark = bookmarks["start_MainActivity"]
        val MainActivityBookmarkStatus = qiChatbot.bookmarkStatus(MainActivityBookmark)


        //when bookmark is reached
        MainActivityBookmarkStatus.addOnReachedListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        chat.run()
    }

    override fun onRobotFocusLost() {
        Log.i(TAG,"Focus Lost")
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.i(TAG,"Focus refused because $reason")
    }


    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this,this)
    }


    //Funktion die Pepper Dinge Sagen lässt
    private fun makeSay(text: String) : Say{
        return SayBuilder.with(qiContext)
            .withText(text)
            .build()
    }
    //Funktion für Animation
    private fun makeAnimate(animResource: Int) : Animate {
        val animation = AnimationBuilder.with(qiContext)
            .withResources(animResource)
            .build()
        return AnimateBuilder.with(qiContext)
            .withAnimation(animation)
            .build()
    }
    //Starte MainActivity über den Button
    fun startMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}