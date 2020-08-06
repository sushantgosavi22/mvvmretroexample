package com.axxessapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.aands.sim.simtoolkit.firebase.DatabaseHelper.addComments
import com.aands.sim.simtoolkit.firebase.DatabaseHelper.getComments
import com.aands.sim.simtoolkit.firebase.DatabaseHelper.onSuccessListener
import com.axxessapp.Model.CommentModel
import com.axxessapp.Utils.showToast
import com.google.firebase.database.DatabaseError
import java.util.*

class ShapeDetailsViewModel(application: Application) : AndroidViewModel(application) {

    fun addComments(context: Context, imageId: String, comment: String, databaseCallBack : databaseCallBack){
        val commentModel = CommentModel()
        commentModel.comment = comment
        commentModel.date = System.currentTimeMillis()
        commentModel.id = UUID.randomUUID().toString()
        commentModel.imageId = imageId
        addComments(context, imageId, commentModel, object : onSuccessListener {
            override fun onSuccess(success: Boolean, data: java.util.ArrayList<CommentModel>?, error: DatabaseError?) {
                if (success) {
                    getCommentFromDatabase(context,imageId,databaseCallBack)
                } else {
                    if (error != null) {
                        showToast(context, error.message)
                    }
                }
            }
        })
    }
    fun getCommentFromDatabase(context: Context, imageId: String, databaseCallBack : databaseCallBack){
        getComments(imageId, context, object : onSuccessListener {
            override fun onSuccess(success: Boolean, data: ArrayList<CommentModel>?, error: DatabaseError?) {
                databaseCallBack.onCallBack(success,data,error)
            }
        })
    }

    public interface databaseCallBack{
       fun onCallBack(success: Boolean?,data : List<CommentModel>?, t: DatabaseError?)
    }

}