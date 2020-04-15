package ke.co.infiware.uaa.useradministration.repositories

import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 *
 * @author Denis Gitonga
 */
interface UserRepository : CoroutineCrudRepository<InfiwareUser, String> {

}