package com.example.tradeapp.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tradeapp.DBkey.Companion.CHILD_CHAT
import com.example.tradeapp.DBkey.Companion.DB_ARTICLES
import com.example.tradeapp.DBkey.Companion.DB_USERS
import com.example.tradeapp.R
import com.example.tradeapp.chat.ChatList
import com.example.tradeapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var articleDB : DatabaseReference
    private lateinit var UserDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private var binding: FragmentHomeBinding? = null

    private val auth : FirebaseAuth by lazy {
        Firebase.auth
    }

    private val articlelist = mutableListOf<ArticleModel>()

    private val listener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return
            articlelist.add(articleModel)
            articleAdapter.submitList(articlelist)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }


    // fragment의 onViewCreated 는 activity 에선 oncreate() 함수와 같은 기능임 생명주기 참고
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentHomeBinding.bind(view)

        binding = fragmentBinding

        articlelist.clear()
        UserDB = Firebase.database.reference.child(DB_USERS)
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if(auth.currentUser != null){
                //로그인을 한 상태
                if(auth.currentUser!!.uid != articleModel.sellerId){
                    val chatroom = ChatList(
                        buyerId = auth.currentUser!!.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    UserDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatroom)

                    UserDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatroom)

                    Snackbar.make(view, "채팅방이 생성되었습니다.",Snackbar.LENGTH_LONG).show()


                }else{
                    Snackbar.make(view, "본인이 올린 글입니다.",Snackbar.LENGTH_LONG).show()

                }

            }else{
                Snackbar.make(view, "로그인 후 사용해 주세요.",Snackbar.LENGTH_LONG).show()
            }
        })

        fragmentBinding.articleRecylcerView.layoutManager = LinearLayoutManager(context)
        fragmentBinding.articleRecylcerView.adapter = articleAdapter

        fragmentBinding.addfloatingButton.setOnClickListener {
            context?.let {
                if(auth.currentUser != null){
                    val intent = Intent(it,AddArticleActivity::class.java)
                    startActivity(intent)
                }else{
                    Snackbar.make(view, "로그인 후 사용해 주세요.",Snackbar.LENGTH_LONG).show()
                }
            }
        }

        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }


}