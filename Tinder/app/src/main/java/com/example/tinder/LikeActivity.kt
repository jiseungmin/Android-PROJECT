package com.example.tinder

import CardStackAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tinder.DBkey.Companion.DIS_LIKE
import com.example.tinder.DBkey.Companion.LIKE
import com.example.tinder.DBkey.Companion.LIKERD_BY
import com.example.tinder.DBkey.Companion.NAME
import com.example.tinder.DBkey.Companion.USERS
import com.example.tinder.DBkey.Companion.USER_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity(), CardStackListener {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private val adapter = CardStackAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val manager by lazy {
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiivity_like)

        userDB = Firebase.database.reference.child(USERS)

        val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(NAME).value == null) {
                    ShowNameInputPopup()
                    return
                }
                getUnSeclectUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        initCardStackView()
        initsignoutButton()
        initmatchlistButton()
    }

    private fun initCardStackView(){
        val stackView = findViewById<CardStackView>(R.id.cardStackView)

        stackView.layoutManager = manager
        stackView.adapter = adapter

    }

    private fun getUnSeclectUsers() {
        userDB.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.child(USER_ID).value != getCurrentUserID()
                    && snapshot.child(LIKERD_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    && snapshot.child(LIKERD_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not()){

                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided"
                    if(snapshot.child("name").value != null){
                        name = snapshot.child("name").value.toString()
                    }
                    cardItems.add(CardItem(userId,name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cardItems.find{it.UserId == snapshot.key }?.let {
                    it.Name = snapshot.child(NAME).value.toString()
                }
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        } )
    }

    private fun ShowNameInputPopup() {
        val EditText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.write_name))
            .setView(EditText)
            .setPositiveButton("저장") { _, _ ->
                if (EditText.text.isEmpty()) {
                    ShowNameInputPopup()
                } else {
                    SaveUserName(EditText.text.toString())
                }
            }
            .setCancelable(false) // 뒤로가기 제한
            .show()

    }

    private fun SaveUserName(name: String) {
        val userId = getCurrentUserID()
        val CurrentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["UserId"] = userId
        user["name"] = name
        CurrentUserDB.updateChildren(user)

        getUnSeclectUsers()
    }


    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어 있지않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun Like(){
        val card = cardItems[manager.topPosition-1]
        cardItems.removeFirst()

        userDB.child(card.UserId)
            .child("likeby")
            .child("Like")
            .child(getCurrentUserID())
            .setValue(true)

        saveMatchIfOtherUserLikedMe(card.UserId)

        Toast.makeText(this, "${card.Name}님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun saveMatchIfOtherUserLikedMe(otherUserID: String){
        val otherUserDB = userDB.child(getCurrentUserID()).child("likeby").child("Like").child(otherUserID)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == true){
                    userDB.child(getCurrentUserID())
                        .child("likeby")
                        .child("match")
                        .child(otherUserID)
                        .setValue(true)

                    userDB.child(otherUserID)
                        .child("likeby")
                        .child("match")
                        .child(getCurrentUserID())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun diLike(){
        val card = cardItems[manager.topPosition-1]
        cardItems.removeFirst()

        userDB.child(card.UserId)
            .child("likeby")
            .child("disLike")
            .child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(this, "${card.Name}님을 disLike 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction){
            Direction.Right -> Like()
            Direction.Left -> diLike()
            else -> {}
        }

    }
    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}

    private fun initsignoutButton() {
        val signoutbutton = findViewById<Button>(R.id.signoutbutton)
        signoutbutton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initmatchlistButton() {
        val matchlistButton = findViewById<Button>(R.id.matchlist)
        matchlistButton.setOnClickListener {
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }
}