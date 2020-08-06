package com.aands.sim.simtoolkit.firebase

import android.content.Context
import com.axxessapp.Model.CommentModel
import com.axxessapp.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import org.w3c.dom.Comment


object DatabaseHelper {

    val TAG = DatabaseHelper.javaClass.name
    private fun getCommentTable(imageId : String) : String{
      return Utils.COMMENT_TABLE.plus("/").plus(imageId)
    }

    public fun getComments(imageId : String,context: Context,listener : onSuccessListener){
        FirebaseHelper.getInstance(context).get(getCommentTable(imageId),object : FirebaseHelper.GetListener{
            override fun onGet(path: String?, data: DataSnapshot?) {
                data?.let {
                    var commentModelList = getListData(data, CommentModel::class.java)
                    listener.onSuccess(true,commentModelList,null)
                }?:listener.onSuccess(false,null,null)
            }
        }).setErrorListener(object :FirebaseHelper.ErrorListener{
            override fun onError(error: DatabaseError) {
                listener.onSuccess(false,null,error)
            }
        })
    }

    public fun addComments(context: Context, imageId: String,commentModel: CommentModel, listener: onSuccessListener){
        FirebaseHelper.getInstance(context).pushById(getCommentTable(imageId),commentModel,object : FirebaseHelper.PushListener{
            override fun onPush(path: String?, ref: DatabaseReference?) {
                listener.onSuccess(true,null,null)
            }
        }).setErrorListener(object :FirebaseHelper.ErrorListener{
            override fun onError(error: DatabaseError) {
                listener.onSuccess(false,null,error)
            }
        })
    }

    interface onSuccessListener{
        fun onSuccess(success: Boolean,data: ArrayList<CommentModel>?,error: DatabaseError?)
    }

    fun <T> getListData(dataSnapshot: DataSnapshot, valueType: Class<T>): ArrayList<T> {
        var list = ArrayList<T>()
        if (null != dataSnapshot.value) {
            for (children in dataSnapshot.children) {
                try {
                    val model = children.getValue(valueType)
                    if (null != model) {
                        list.add(model)
                    }
                } catch (e: Exception) {
                   e.printStackTrace()
                }
            }
        }
        return list
    }

    fun <T> getClassData(dataSnapshot: DataSnapshot, valueType: Class<T>): T? {
        var data: T? = null
        try {
            if (null != dataSnapshot.value) {
                var nullableData = dataSnapshot.getValue(valueType)
                nullableData?.apply {
                    data = this
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}