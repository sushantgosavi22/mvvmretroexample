package com.aands.sim.simtoolkit.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.io.Serializable
import java.util.*

/**
 * This class provides a simplified interface to the Firebase remote database system.
 * Methods require the context such as an `Activity`, so you
 * must call `SimpleFirebase.with(yourActivity)` first.
 */
class FirebaseHelper {
    /**
     * An event listener that can respond to database errors.
     */
    interface ErrorListener {
        fun onError(error: DatabaseError)
    }

    /**
     * An event listener that can respond to the result of get() calls.
     */
    interface GetListener {
        fun onGet(path: String?, data: DataSnapshot?)
    }

    /**
     * An event listener that can respond to the result of push() or pushById() calls.
     */
    interface PushListener {
        fun onPush(path: String?, ref: DatabaseReference?)
    }

    /**
     * An event listener that can respond to the result of set() calls.
     */
    interface SetListener {
        fun onSet(path: String?)
    }

    /**
     * An event listener that can respond to the result of signIn() calls.
     */
    interface SignInListener {
        fun onSignIn(successful: Boolean)
    }

    /**
     * An event listener that can respond to the result of transaction() calls.
     */
    interface TransactionListener {
        fun onTransaction(path: String?, mdata: MutableData?)
    }

    /**
     * An event listener that can respond to the result of watch() calls.
     */
    interface WatchListener {
        fun onDataChange(data: DataSnapshot?)
    }

    private var context // activity/fragment used to load resources
            : Context? = null
    private var errorListener // responds to database errors (null if none)
            : ErrorListener? = null
    private var lastDatabaseError: DatabaseError? = null // last database error that occurred (null if none)
    private var lastQueryPath: String? = null // last string/Query from get()/etc.
    private var lastQuery: Query? = null
    /**
     * Returns true if a user is currently signed in.
     */
    val isSignedIn = false // true if finished signing in to db
    private var logging = false // true if we should Log various debug messages
    private var keepSynced = false // true if we want to sync local database with server
    /**
     * Returns a child of the overall database; equivalent to Firebase's child() method
     * or the SimpleFirebase query() method.
     *
     * @see FirebaseHelper.query
     */
    fun child(queryText: String?): DatabaseReference {
        return query(queryText)
    }

    /**
     * Clears this object's record of any past database error.
     * If there was no past error, has no effect.
     */
    fun clearLastDatabaseError() {
        lastDatabaseError = null
    }
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    @JvmOverloads
    operator fun get(path: String?, listener: GetListener? =  /* listener */null): FirebaseHelper {
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        val child = fb.child(path!!)
        return getWatchHelper(path, child, listener,  /* watch */false)
    }
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     *
     * @param ref      a Query object containing an absolute database reference
     * @param listener object to notify when the data has arrived
     */
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param ref a Query object containing an absolute database reference
     */
    @JvmOverloads
    operator fun get(ref: Query?, listener: GetListener? =  /* listener */null): FirebaseHelper {
        return getWatchHelper( /* path */null, ref, listener,  /* watch */false)
    }

    // common helper code for all overloads of get() and watch()
    private fun getWatchHelper(path: String?, ref: Query?, listener: GetListener?, watch: Boolean): FirebaseHelper {
        var path = path
        var listener = listener
        if (ref == null) {
            return this
        } else if (path == null) {
            if (ref === lastQuery) {
                path = lastQueryPath
            } else {
                lastQuery = ref
                lastQueryPath = null
                path = ref.toString()
            }
        }
        if (logging) {
            Log.d(LOG_TAG, "get/watch: path=$path")
        }
        // listen to the data coming back
        if (listener == null && context is GetListener) {
            listener = context as GetListener?
        }
        val valueListener = InnerValueEventListener()
        valueListener.path = path
        valueListener.getListener = listener
        // either listen once (get) or keep listening (watch)
        if (watch) {
            ref.addValueEventListener(valueListener)
        } else {
            ref.addListenerForSingleValueEvent(valueListener)
        }
        return this
    }

    /*
     * Helper function to check for database errors and call listeners as needed.
     * Returns true if there was an error, false if not.
     */
    private fun handleDatabaseError(error: DatabaseError?): Boolean {
        return if (error != null) {
            lastDatabaseError = error
            if (errorListener != null) {
                errorListener!!.onError(error)
            } else if (context is ErrorListener) {
                (context as ErrorListener).onError(error)
            }
            true
        } else {
            false
        }
    }

    /**
     * Returns true if there has been a database error that has not been cleared.
     */
    fun hasLastDatabaseError(): Boolean {
        return lastDatabaseError != null
    }

    /**
     * Returns the last database error that occurred, or null if no error has occurred.
     */
    fun lastDatabaseError(): DatabaseError? {
        return lastDatabaseError
    }

    /**
     * Adds a new object with a randomly-generated unique string key at the given path in the database,
     * and returns that newly pushed object.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    fun push(path: String?): DatabaseReference {
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        return fb.child(path!!).push()
    }
    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be temporarily given a value of boolean 'false'.
     * The given PushListener will be notified when the data has been created.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * This call will query the given path to find the currently largest child ID, and set the newly
     * added child to have an ID that is +1 higher than that largest child ID.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the PushListener interface, it will be notified when the new object's key is found
     * and the new object has been created.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    @JvmOverloads
    fun pushById(path: String, listener: PushListener? =  /* listener */null): FirebaseHelper {
        return pushById(path,  /* value */false, listener)
    }

    fun updateChildren(path: String, value: HashMap<String, Any>) {
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        val child = fb.child(path)
        value.entries.forEach {
            set(path,it.key,it.value)
        }
    }
    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * The given PushListener will be notified when the data has been created.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param value    value to store at this path
     * @param listener object to notify when the data has arrived
     */
    /**
     * Adds a new object with a new unique integer ID key at the given path in the database.
     * The object will be stored with the given initial value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the PushListener interface, it will be notified when the new object's key is found
     * and the new object has been created.
     *
     * @param path  absolute database path such as "foo/bar/baz"
     * @param value value to store at this path
     */
    @JvmOverloads
    fun pushById(path: String, value: Any, listener: PushListener? =  /* listener */null): FirebaseHelper {
        var listener = listener
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        val child = fb.child(path)
        if (logging) {
            Log.d(LOG_TAG, "push: path=$path, value=$value")
        }
        if (listener == null && context is PushListener) {
            listener = context as PushListener?
        }
        // query to get largest current ID (may need to repeat)
        pushById_getMaxId(path, value, child, listener)
        return this
    }

    /*
     * Helper that queries the db to find the max numeric ID in given area.
     * Once found, tries to start a transaction to add a new child with next available ID.
     */
    private fun pushById_getMaxId(path: String, value: Any,
                                  child: DatabaseReference, listener: PushListener?) {
        val query = child.orderByKey().limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                var key: Long = 0
                if (!data.hasChildren()) { // this will be the first child
                    key = 0
                    if (value is BaseFirebaseModel) {
                        value.firebaseId = key
                    }
                    pushById_addNewChild(path, value, child, key, listener)
                } else {
                    val lastChild = data.children.iterator().next()
                    val keyStr = lastChild.key
                    try {
                        key = keyStr!!.toLong() + 1 // increment to next key
                        if (value is BaseFirebaseModel) {
                            value.firebaseId = key
                        }
                        pushById_addNewChild(path, value, child, key, listener)
                    } catch (nfe: NumberFormatException) { // empty
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handleDatabaseError(error)
            }
        })
    }

    /*
     * Starts a transaction to add a new child with the given ID.
     * If the ID is taken by the time we get the transaction lock, retries
     * by querying again to get the next available ID.
     */
    private fun pushById_addNewChild(path: String, value: Any,
                                     ref: DatabaseReference, idKey: Long, listener: PushListener?) {
        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mdata: MutableData): Transaction.Result { // add the new child
                Log.d("SimpleFirebase", "doTransaction: mdata=$mdata")
                return if (mdata.hasChild(idKey.toString())) { // oops; somebody already claimed this ID; retry!
                    pushById_getMaxId(path, value, ref, listener)
                    Transaction.abort()
                } else {
                    val newChild = mdata.child(idKey.toString())
                    newChild.value = value
                    Transaction.success(mdata)
                }
            }

            override fun onComplete(error: DatabaseError?, completed: Boolean, data: DataSnapshot?) {
                Log.d("SimpleFirebase", "transaction onComplete: error=$error, completed=$completed, data=$data")
                if (!handleDatabaseError(error) && completed && listener != null) {
                    val childPath = path + (if (path.endsWith("/")) "" else "/") + idKey
                    val childRef = ref.child(idKey.toString())
                    listener.onPush(childPath, childRef)
                }
            }
        })
    }

    /**
     * Performs a query on the Firebase database.
     * Similar to the Firebase child() method.
     * Common intended usage:
     *
     * <pre>
     * SimpleFirebase fb = SimpleFirebase.with(this);
     * fb.get(fb.query("foo/bar/baz")
     * .orderByChild("quux")
     * .limitToFirst(1));
    </pre> *
     *
     * @param queryText absolute path in database such as "foo/bar/baz"
     */
    fun query(queryText: String?): DatabaseReference {
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        val query = fb.child(queryText!!)
        lastQuery = query
        lastQueryPath = queryText
        return query
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    operator fun set(path: String, value: Any): FirebaseHelper {
        return setHelper(path,  /* key */"", value,  /* listener */null)
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param key   child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    operator fun set(path: String, key: String?, value: Any): FirebaseHelper {
        return setHelper(path, key, value,  /* listener */null)
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    operator fun set(path: String, value: Any, listener: SetListener?): FirebaseHelper {
        return setHelper(path,  /* key */"", value,  /* listener */listener)
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     *
     * @param path  an absolute database path such as "foo/bar/baz"
     * @param key   child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    operator fun set(path: String, key: String?, value: Any, listener: SetListener?): FirebaseHelper {
        return setHelper(path, key, value,  /* listener */listener)
    }

    // helper for common set() code
    private fun setHelper(path: String, key: String?, value: Any, listener: SetListener?): FirebaseHelper {
        var path = path
        var listener = listener
        if (listener == null && context is SetListener) {
            listener = context as SetListener?
        }
        if (logging) {
            Log.d(LOG_TAG, "set: path=$path, key=$key, value=$value")
        }
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        var child: DatabaseReference? = null
        if (key == null || key.isEmpty()) {
            child = fb.child(path)
        } else {
            if (!path.endsWith("/")) {
                path += "/"
            }
            path += key
            child = fb.child(path)
        }
        if (child == null) {
            return this
        }
        if (listener != null) {
            val myListener = InnerCompletionListener()
            myListener.path = path
            myListener.set = listener
            child.setValue(value, myListener)
        } else {
            child.setValue(value)
        }
        return this
    }

    /**
     * Sets the given listener object to be notified of future database errors.
     * Pass null to disable listening for database errors.
     * If the context passed to with() implements ErrorListener, it will be automatically
     * notified of database errors even if you don't call setErrorListener.
     */
    fun setErrorListener(listener: ErrorListener?): FirebaseHelper {
        errorListener = listener
        return this
    }

    /**
     * Sets whether the SimpleFirebase library should print log messages for debugging.
     */
    fun setLogging(logging: Boolean): FirebaseHelper {
        this.logging = logging
        if (errorListener == null) { // set up a default error logging listener if there is none
            errorListener = InnerErrorListener()
        }
        return this
    }
    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * The given TransactionListener will be notified when the mutable data has arrived.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the mutable data has arrived
     */
    /**
     * Initiates a request to perform a transaction on the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the TransactionListener interface, it will be notified when the mutable data has arrived.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    @JvmOverloads
    fun transaction(path: String?, listener: TransactionListener? =  /* listener */null): FirebaseHelper {
        val fb = FirebaseDatabase.getInstance().reference
        fb.keepSynced(keepSynced)
        val child = fb.child(path!!)
        // query to get largest current ID
        val handler = InnerTransactionHandler()
        handler.path = path
        if (listener != null) {
            handler.listener = listener
        } else if (context is TransactionListener) {
            handler.listener = context as TransactionListener?
        }
        child.runTransaction(handler)
        return this
    }
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param path     absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param path absolute database path such as "foo/bar/baz"
     */
    @JvmOverloads
    fun watch(path: String?, listener: GetListener? =  /* listener */null): FirebaseHelper? {
        path?.let {
            val fb = FirebaseDatabase.getInstance().reference
            fb.keepSynced(keepSynced)
            val child = fb.child(path)
            lastQuery = child
            lastQueryPath = path
            return getWatchHelper(path, child, listener,  /* watch */true)
        }
       return null
    }

    @JvmOverloads
    fun watchChild(path: String?,childEventListener: ChildEventListener) {
        path?.let {
            val fb = FirebaseDatabase.getInstance().reference
            fb.keepSynced(keepSynced)
            val child = fb.child(path)
            child.addChildEventListener(childEventListener)
        }
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     *
     * @param ref      a Query object representing an absolute database path
     * @param listener object to notify when the data has arrived
     */
    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     *
     * @param ref a Query object representing an absolute database path
     */
    @JvmOverloads
    fun watch(ref: Query?, listener: GetListener? =  /* listener */null): FirebaseHelper {
        return getWatchHelper( /* path */null, ref, listener,  /* watch */true)
    }

    /*
     * Helper class that listens for database task completion results; used by set().
     */
    private inner class InnerCompletionListener : DatabaseReference.CompletionListener {
        private var complete = false
        private val error: DatabaseError? = null
        var set: SetListener? = null
        var path: String? = null
        override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
            complete = true
            set?.onSet(path)
            handleDatabaseError(error)
        }
    }

    /*
     * Helper class that listens for database errors and logs them to the Android Studio console.
     */
    private inner class InnerErrorListener : ErrorListener {
        override fun onError(error: DatabaseError) {
            Log.d(LOG_TAG, " *** DATABASE ERROR: $error")
        }
    }

    private inner class InnerTransactionHandler : Transaction.Handler {
        var path: String? = null
        var listener: TransactionListener? = null
        override fun doTransaction(mdata: MutableData): Transaction.Result {
            listener?.onTransaction(path, mdata)
            return Transaction.success(mdata)
        }

        override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {
            handleDatabaseError(error)
        }
    }

    /*
     * Helper class that listens for data arrival results; used by get() and watch().
     */
    private inner class InnerValueEventListener : ValueEventListener {
        var path: String? = null
        var getListener: GetListener? = null
        override fun onDataChange(data: DataSnapshot) {
            getListener?.onGet(path, data)
        }

        override fun onCancelled(error: DatabaseError) {
            handleDatabaseError(error)
        }
    }

    fun setKeepSynced(keepSynced: Boolean) {
        this.keepSynced = keepSynced
    }

    companion object {
        // tag for debug logging
        private const val LOG_TAG = "SimpleFirebase"
        // whether the Firebase db has been initialized
        private var sInitialized = false

        /**
         * Returns a new SimpleFirebase instance using the given activity or other context.
         */
        fun getInstance(context: Context?): FirebaseHelper {
            val fb = FirebaseHelper()
            fb.context = context
            fb.setKeepSynced(true)
            fb.setLogging(true)
            if (!sInitialized) {
                synchronized(FirebaseHelper::class.java) {
                    if (!sInitialized) {
                        FirebaseApp.initializeApp(context!!)
                        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                        sInitialized = true
                    }
                }
            }
            return fb
        }
    }
}

open class BaseFirebaseModel(var firebaseId: Long =0) : Serializable