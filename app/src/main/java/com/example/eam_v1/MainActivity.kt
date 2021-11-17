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


private const val TAG = "MainActivity"

class MainActivity : RobotActivity(), RobotLifecycleCallbacks {
    // Store the Chat action.
    //private lateinit var chat: Chat
    private var qiContext: QiContext? =null
    //Store the greet BookmarkStatus
    private lateinit var reiseAuskunftBookmarkStatus: BookmarkStatus
    private lateinit var ortsAuskunftBookmarkStatus: BookmarkStatus
    private lateinit var smallTalkBookmarkStatus: BookmarkStatus
    private lateinit var reisePlanungBookmarkStatus: BookmarkStatus
    private lateinit var endConversationBookmarkStatus: BookmarkStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"on Create")
        super.onCreate(savedInstanceState)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setContentView(R.layout.activity_main)
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

    override fun onRobotFocusGained(qiContext: QiContext) {
        this.qiContext = qiContext
        //Autonome Abilites starten
        holdAbilities(qiContext)
        makeSay("Brauchst du eine Reiseauskunft, eine Ortsauskunft, eine Reiseplanung oder nur Small Talk ?")

        //Festlegen der Sprache des Roboters//
        val locale = com.aldebaran.qi.sdk.`object`.locale.Locale(Language.GERMAN, Region.GERMANY)
        //Einbinden des zuvor erstellten Topic
        val topic = TopicBuilder.with(qiContext).withResource(R.raw.mainactivity).build()
        // Erstellen eines qiChatbos mittels Chatbotbilder und unter Einbeziehung der Spracheinstellungen
        val qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).withLocale(locale).build()
        //Chat erstellen
        val chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).withLocale(locale).build()


        // bookmarks stuff
        val bookmarks = topic.bookmarks

        //Get  bookmark
        val reiseAuskunftBookmark = bookmarks["start_reiseauskunft"]
        val reiseAuskunftBookmarkStatus = qiChatbot.bookmarkStatus(reiseAuskunftBookmark)
        val ortsAuskunftTalkBookmark = bookmarks["start_ortsauskunft"]
        val ortsAuskunftTalkBookmarkStatus = qiChatbot.bookmarkStatus(ortsAuskunftTalkBookmark)
        val smallTalkBookmark = bookmarks["start_smalltalk"]
        val smallTalkBookmarkStatus = qiChatbot.bookmarkStatus(smallTalkBookmark)
        val reisePlanungBookmark = bookmarks["start_reiseplanung"]
        val reisePlanungBookmarkStatus = qiChatbot.bookmarkStatus(reisePlanungBookmark)
        val endConversationBookmark = bookmarks["start_startscreen"]
        val endConversationBookmarkStatus = qiChatbot.bookmarkStatus(endConversationBookmark)


        //when bookmark is reached
        reiseAuskunftBookmarkStatus.addOnReachedListener {
            val intent = Intent(this, ReiseAuskunft::class.java)
            startActivity(intent)
        }
        this.reiseAuskunftBookmarkStatus = reiseAuskunftBookmarkStatus

        ortsAuskunftTalkBookmarkStatus.addOnReachedListener {
            val intent = Intent(this, OrtsAuskunft::class.java)
            startActivity(intent)
        }
        this.ortsAuskunftBookmarkStatus = ortsAuskunftTalkBookmarkStatus

        smallTalkBookmarkStatus.addOnReachedListener {
           val intent = Intent(this, SmallTalk::class.java)
               startActivity(intent)
        }
        this.smallTalkBookmarkStatus = smallTalkBookmarkStatus

        reisePlanungBookmarkStatus.addOnReachedListener {
            val intent = Intent(this, ReisePlanung::class.java)
            startActivity(intent)
        }
        this.reisePlanungBookmarkStatus = reisePlanungBookmarkStatus

        endConversationBookmarkStatus.addOnReachedListener {
            val intent = Intent(this, StartScreen::class.java)
            startActivity(intent)
        }
        this.endConversationBookmarkStatus = endConversationBookmarkStatus



        chat.run()
    }

    override fun onRobotFocusLost() {
        Log.i(TAG,"Focus Lost")
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.i(TAG,"Focus refused because $reason")
    }


    fun startSmallTalk(view: View) {
        //Starte die Activity Small Talk
        val intent = Intent(this, SmallTalk::class.java)
        startActivity(intent)
    }

    fun startReiseAuskunft(view: View){
        //Starte die Activity Reise Auskunft
        val intent = Intent(this, ReiseAuskunft::class.java)
        startActivity(intent)
    }

    fun startReisePlanung(view: View){
        //Starte die Activity Reise Planung
        val intent = Intent(this, ReisePlanung::class.java)
        startActivity(intent)
    }

    fun startOrtsAuskunft(view: View){
        //Starte die Activity Orts Auskunft
        val intent = Intent(this, OrtsAuskunft::class.java)
        startActivity(intent)
    }

    fun startExit(view: View){
        //Starte die Activity Orts Auskunft
        val intent = Intent(this, StartScreen::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this,this)
    }

}