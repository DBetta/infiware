package ke.co.infiware.uaa.useradministration.repositories

import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
interface InfiwareUserRepository : JpaRepository<InfiwareUser, UUID> {

    fun findByEmailAddress(emailAddress: String?): InfiwareUser?

    fun findByContactNumber(contactNumber: String?): InfiwareUser?

    fun findByUsername(username: String?): InfiwareUser?
}