package ke.co.infiware.uaa.useradministration.enities

import com.github.pozo.KotlinBuilder
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 *
 * @author Denis Gitonga
 */
@KotlinBuilder
data class InfiwareUser (
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val code: UUID? = null
)