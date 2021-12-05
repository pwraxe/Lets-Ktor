import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.codexdroid.UserData
import io.ktor.config.*

class TokenManager(val config: HoconApplicationConfig) {

    private val ONE_MIN = 60_000

    fun generateJwt(userData: UserData) : String{
        val audience = config.property("audience").getString()
        val secret = config.property("secret").getString()
        val issuer = config.property("issuer").getString()
        val tokenExpiration = System.currentTimeMillis() + ONE_MIN   //Token expired after every one minute

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username",userData.username)
            .withClaim("password",userData.password)
            .sign(Algorithm.HMAC256(secret))

        return token

    }
}