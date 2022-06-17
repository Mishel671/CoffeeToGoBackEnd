import kotlinx.serialization.Serializable

@Serializable
data class User(val login:String, var password: String, var mail: String? = "",var name_image: String? = "profile.png")

@Serializable
data class OpenUserInformation(val login:String,var mail: String?)

@Serializable
data class RespondUser(var login: String = "", var token: String = "", var mail:String = "", var name_icon: String = "")

@Serializable
data class UserAuthorization(val login: String, val password: String)

val users = mutableListOf<User>(
    User("root","toor", "rootMail", "profile.png"),
)

val accounts = mutableListOf<OpenUserInformation>()



