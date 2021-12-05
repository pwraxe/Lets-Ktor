import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.codexdroid.UserData
import io.ktor.config.*
import java.util.*

class TokenManager(val config: HoconApplicationConfig) {

    private val oneSec = 1_000
    private val oneMin = 60_000
    private val oneHr = oneMin * 60
    private val eightHr = oneHr * 8

    private val audience = config.property("audience").getString()
    private val secret = config.property("secret").getString()
    private val issuer = config.property("issuer").getString()
    private val tokenExpiration = System.currentTimeMillis() + eightHr   //Token expired after every eight Hr

    fun generateJwt(userData: UserData) : String{

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username",userData.username)
            .withClaim("password",userData.password)
            .withExpiresAt(Date(tokenExpiration))
            .sign(Algorithm.HMAC256(secret))

        return token
    }


    fun verifyToken() : JWTVerifier{
        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}
