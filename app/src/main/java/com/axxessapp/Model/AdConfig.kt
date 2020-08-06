import com.google.gson.annotations.SerializedName

data class AdConfig (
		@SerializedName("safeFlags") val safeFlags : List<String>  = ArrayList(),
		@SerializedName("highRiskFlags") val highRiskFlags : List<String> = ArrayList(),
		@SerializedName("unsafeFlags") val unsafeFlags : List<String> = ArrayList(),
		@SerializedName("wallUnsafeFlags") val wallUnsafeFlags : List<String> = ArrayList(),
		@SerializedName("showsAds") val showsAds : Boolean = false
)