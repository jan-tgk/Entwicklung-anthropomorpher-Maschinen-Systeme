package com.example.eam_v1


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.BookmarkStatus
import com.aldebaran.qi.sdk.`object`.holder.AutonomousAbilitiesType
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.HolderBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy

private const val TAG = "Reise Auskunft"

class ReiseAuskunft : RobotActivity() ,RobotLifecycleCallbacks{

    private var qiContext: QiContext? =null

    //Store the greet BookmarkStatus
    private lateinit var reiseAuskunftBookmarkStatus: BookmarkStatus
    private lateinit var ortsAuskunftBookmarkStatus: BookmarkStatus
    private lateinit var smallTalkBookmarkStatus: BookmarkStatus
    private lateinit var reisePlanungBookmarkStatus: BookmarkStatus
    private lateinit var endConversationBookmarkStatus: BookmarkStatus

    private lateinit var showPassageB_BookmarkStatus: BookmarkStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setContentView(R.layout.activity_reise_auskunft)
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

        //Festlegen der Sprache des Roboters//
        val locale = com.aldebaran.qi.sdk.`object`.locale.Locale(Language.GERMAN, Region.GERMANY)
        //Einbinden des zuvor erstellten Topic
        val topic = TopicBuilder.with(qiContext).withResource(R.raw.reise_auskunft).build()
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
        val showPassageB_Bookmark = bookmarks["show_passage_b"]
        val showPassageB_BookmarkStatus = qiChatbot.bookmarkStatus(showPassageB_Bookmark)

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

        showPassageB_BookmarkStatus.addOnReachedListener {
            val imageView = findViewById<ImageView>(R.id.imageView_ReiseAuskunft)
            imageView.setImageResource(R.drawable.weg_zu_passage_b)
        }
        this.showPassageB_BookmarkStatus = showPassageB_BookmarkStatus


        //Ausführen des Chats
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

    fun startMainActivity(view: View){
        //Starte die Activity Reise Auskunft
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun startSmallTalk(view: View) {
        //Starte die Activity Small Talk
        val intent = Intent(this, SmallTalk::class.java)
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
}



