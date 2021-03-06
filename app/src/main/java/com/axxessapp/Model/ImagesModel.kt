import com.google.gson.annotations.SerializedName

data class ImagesModel (
		@SerializedName("id") val id : String?= "",
		@SerializedName("title") val title : String?= "",
		@SerializedName("description") val description : String?= "",
		@SerializedName("datetime") val datetime : Int?= 0,
		@SerializedName("type") val type : String?= "",
		@SerializedName("animated") val animated : Boolean =false,
		@SerializedName("width") val width : Int?= 0,
		@SerializedName("height") val height : Int?= 0,
		@SerializedName("size") val size : Int?= 0,
		@SerializedName("views") val views : Int?= 0,
		@SerializedName("bandwidth") val bandwidth : Int?= 0,
		@SerializedName("vote") val vote : String?= "",
		@SerializedName("favorite") val favorite : Boolean =false,
		@SerializedName("nsfw") val nsfw : String?= "",
		@SerializedName("section") val section : String?= "",
		@SerializedName("account_url") val account_url : String?= "",
		@SerializedName("account_id") val account_id : String?= "",
		@SerializedName("is_ad") val is_ad :  Boolean =false,
		@SerializedName("in_most_viral") val in_most_viral :  Boolean =false,
		@SerializedName("has_sound") val has_sound :  Boolean =false,
		@SerializedName("tags") val tags : List<String?> = ArrayList(),
		@SerializedName("ad_type") val ad_type : Int?= 0,
		@SerializedName("ad_url") val ad_url : String?= "",
		@SerializedName("edited") val edited : Int?= 0,
		@SerializedName("in_gallery") val in_gallery :  Boolean =false,
		@SerializedName("link") val link : String?= "",
		@SerializedName("comment_count") val comment_count : String?= "",
		@SerializedName("favorite_count") val favorite_count : String?= "",
		@SerializedName("ups") val ups : String?= "",
		@SerializedName("downs") val downs : String?= "",
		@SerializedName("points") val points : String?= "",
		@SerializedName("score") val score : String?= ""
)