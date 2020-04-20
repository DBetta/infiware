package ke.co.infiware.uaa.useradministration.enities

import com.github.pozo.KotlinBuilder
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

/**
 *
 * @author Denis Gitonga
 */
@Entity
@Table(name = "inf_users")
@KotlinBuilder
data class InfiwareUser(
        @Id
        @Column(name = "usr_code")
        @Type(type = "uuid-char")
        @GeneratedValue(generator="uuid2")
        @GenericGenerator(name="uuid2", strategy = "uuid2")
        val code: UUID? = null,

        @Column(name = "usr_username")
        val username: String? = null,

        @Column(name = "usr_first_name")
        val firstName: String? = null,

        @Column(name = "usr_middle_name")
        val middleName: String? = null,

        @Column(name = "usr_last_name")
        val lastName: String? = null,

        @Column(name = "usr_email")
        val emailAddress: String? = null,

        @Column(name = "usr_contact_number")
        val contactNumber: String? = null,

        @Column(name = "usr_password")
        val password: String? = null,

        @Column(name = "usr_photo")
        val photoUrl: String? = null,

        @Column(name = "usr_provider")
        val provider: String? = "default",

        @Column(name = "usr_provider_id")
        val providerId: String? = null,

        @Column(name = "usr_is_verified")
        val verified: Boolean = false,

        @Column(name = "usr_is_deleted")
        val deleted: Boolean = false,

        @Column(name = "usr_is_blocked")
        val blocked: Boolean = false,

        @Version
        val version: Long? = null


)