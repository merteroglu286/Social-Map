package com.example.socialmap.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmap.Adapter.ChatAdapter
import com.example.socialmap.BR
import com.example.socialmap.ChatListModel
import com.example.socialmap.MessageModel
import com.example.socialmap.Models.Chat
import com.example.socialmap.R
import com.example.socialmap.Util.AppUtil
import com.example.socialmap.databinding.ActivityMessageBinding
import com.example.socialmap.databinding.FragmentGetUserDataBinding
import com.example.socialmap.databinding.LeftItemLayoutBinding
import com.example.socialmap.databinding.RightItemLayoutBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*

class MessageActivity : AppCompatActivity() {

    private lateinit var activityMessageBinding: ActivityMessageBinding
    private var hisId : String? =  null
    private var hisImage : String? =  null
    private var chatId : String? =  null
    private lateinit var appUtil : AppUtil
    private lateinit var myId : String
    private lateinit var myImage : String
    private lateinit var sharedPreferences: SharedPreferences
    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMessageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(activityMessageBinding.root)

        appUtil = AppUtil()
        myId = appUtil.getUID()!!
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
        myImage = sharedPreferences.getString("myImage","").toString()

        activityMessageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        activityMessageBinding.activity = this

        hisId = intent.getStringExtra("hisId")
        hisImage = intent.getStringExtra("hisImage")

        activityMessageBinding.btnSend.setOnClickListener {
            var message:String = activityMessageBinding.msgText.text.toString()

            if(message.isEmpty()){

            }else{
                sendMessage(myId,hisId!!,message)
            }
        }

        /*

        activityMessageBinding.btnSend.setOnClickListener {
            val message = activityMessageBinding.msgText.text.toString()
            if(message.isEmpty()){
                Toast.makeText(this,"Mesajınınızı giriniz",Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(message)
            }
        }

        if(chatId != null){
            checkChat(hisId!!)
        }

         */

        readMessage(myId, hisId!!)

    }
    /*

  private fun checkChat(hisId:String){
      val databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId)
      val query = databaseReference.orderByChild("member").equalTo(hisId)
      query.addValueEventListener(object : ValueEventListener{
          override fun onDataChange(snapshot: DataSnapshot) {
              if (snapshot.exists()){
                  for (ds in snapshot.children){
                      val member = ds.child("member").value.toString()
                      if (hisId == member){
                          chatId = ds.key
                          readMessages(chatId!!)
                          break
                      }
                  }
              }
          }

          override fun onCancelled(error: DatabaseError) {
              TODO("Not yet implemented")
          }
      })
  }

  private fun createChat(message: String){
      var databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId)
      chatId = databaseReference.push().key
      val chatListModel = ChatListModel(chatId!!,message,System.currentTimeMillis().toString(),hisId!!)

      databaseReference.child(chatId!!).setValue(chatListModel)
      databaseReference= FirebaseDatabase.getInstance().getReference("ChatList").child(hisId!!)

      val chatList = ChatListModel(chatId!!,message,System.currentTimeMillis().toString(),myId)

      databaseReference.child(chatId!!).setValue(chatList)

      databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)

      val messageModel = MessageModel(myId,hisId!!,message, type = "text")
      databaseReference.push().setValue(messageModel)

  }


  private fun sendMessage(message: String){
      if(chatId == null){
          createChat(message)
      }else{
          var databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(chatId!!)
          val messageModel = MessageModel(myId,hisId!!,message, type = "text")
          databaseReference.push().setValue(messageModel)

          val map: MutableMap<String,Any> = HashMap()
          map["lastMessage"] = message
          map["date"] = System.currentTimeMillis().toString()

          databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(myId).child(chatId!!)

          databaseReference.updateChildren(map)

          databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(hisId!!).child(chatId!!)

          databaseReference.updateChildren(map)
      }
  }

  private fun readMessages(chatId: String) {

      val query = FirebaseDatabase.getInstance().getReference("Chat").child(chatId)

      val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<MessageModel>()
          .setLifecycleOwner(this)
          .setQuery(query,MessageModel::class.java)
          .build()
      query.keepSynced(true)

      firebaseRecyclerAdapter =
          object : FirebaseRecyclerAdapter<MessageModel, ViewHolder>(firebaseRecyclerOptions) {
              override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

                  var viewDataBinding: ViewDataBinding? = null

                  if (viewType == 0)
                      viewDataBinding = RightItemLayoutBinding.inflate(
                          LayoutInflater.from(parent.context),
                          parent,
                          false
                      )

                  if (viewType == 1)

                      viewDataBinding = LeftItemLayoutBinding.inflate(
                          LayoutInflater.from(parent.context),
                          parent,
                          false
                      )

                  return ViewHolder(viewDataBinding!!)

              }

              override fun onBindViewHolder(
                  holder: ViewHolder,
                  position: Int,
                  messageModel: MessageModel
              ) {
                  if (getItemViewType(position) == 0) {
                      holder.viewDataBinding.setVariable(BR.message, messageModel)
                      holder.viewDataBinding.setVariable(BR.messageImage, myImage)
                  }

                  if (getItemViewType(position) == 1) {

                      holder.viewDataBinding.setVariable(BR.message, messageModel)
                      holder.viewDataBinding.setVariable(BR.messageImage, hisImage)
                  }
              }

              override fun getItemViewType(position: Int): Int {

                  val messageModel = getItem(position)
                  return if (messageModel.senderId == myId)
                      0
                  else
                      1
              }
          }

      activityMessageBinding.messageRecyclerView.layoutManager = LinearLayoutManager(this)
      activityMessageBinding.messageRecyclerView.adapter = firebaseRecyclerAdapter
      firebaseRecyclerAdapter!!.startListening()

  }

  class ViewHolder(var viewDataBinding: ViewDataBinding) :
      RecyclerView.ViewHolder(viewDataBinding.root)

  override fun onPause() {
      super.onPause()
      if (firebaseRecyclerAdapter != null){
          firebaseRecyclerAdapter!!.stopListening()
      }
  }
*/


  fun userInfo() {
      val intent = Intent(this, UserInfoActivity::class.java)
      intent.putExtra("userId", hisId)
      startActivity(intent)
  }

    private fun sendMessage(senderId:String , receiverId: String, message:String){

        var reference = FirebaseDatabase.getInstance().getReference()

        var hashMap: HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)
        reference!!.child("Chat").push().setValue(hashMap)
    }


    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@MessageActivity, chatList)

                activityMessageBinding.messageRecyclerView.adapter = chatAdapter
            }
        })
    }


}
