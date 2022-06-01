package studio.sanguine.loanshark2.data

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.io.Serializable

//will be generated by a master query
@DatabaseView(
    "SELECT debtId, DebtRecordDb.contactId, debtAmount, debtIsCreditor, debtDescription, " +
            "debtInitialDate, debtDueDate, interestRate, interestType, " +
            "firstName, lastName, phoneNumbers, " +
            "(SELECT SUM(paymentAmount) FROM History WHERE History.debtId = DebtRecordDb.debtId) as paidAmount" +
    " FROM DebtRecordDb INNER JOIN Contact ON DebtRecordDB.contactId = Contact.contactId")
data class DebtRecordFull (
                           //debt
                           val debtId: Int,
                           val contactId: Int,
                           val debtAmount: Double,
                           val debtIsCreditor: Boolean,
                           val debtDescription: String,
                           val debtInitialDate : String,
                           val debtDueDate: String,
                           val interestRate: Double,
                           val interestType: String,
                           //contact
                           val firstName: String,
                           val lastName: String,
                           val phoneNumbers: String,
                           //select sum(paymentAmount) from history where debtId = :debtId
                           val paidAmount: Double
                           ) : Serializable {
                                fun toDebtRecord() : DebtRecordDb{
                                    return DebtRecordDb(
                                        debtId,
                                        contactId,
                                        debtAmount,
                                        debtIsCreditor,
                                        debtDescription,
                                        debtInitialDate,
                                        debtDueDate,
                                        interestRate,
                                        interestType
                                    )
                                }
                                fun toContactRecord() : Contact{
                                    return Contact(
                                        contactId,
                                        firstName,
                                        lastName,
                                        phoneNumbers
                                    )
                                }
                            }

@DatabaseView(
    "SELECT contactId, firstName, lastName, phoneNumbers, " +
            "(SELECT SUM(debtAmount) FROM DebtRecordDb " +
            "WHERE DebtRecordDb.contactId = Contact.contactId AND DebtRecordDb.debtIsCreditor = 0) as borrowerDebt, " +
            "(SELECT SUM(debtAmount) FROM DebtRecordDb " +
            "WHERE DebtRecordDb.contactId = Contact.contactId AND DebtRecordDb.debtIsCreditor = 1) as creditorDebt " +
            "FROM Contact"
)
data class ContactRecordFull(val contactId: Int,
                             val firstName: String,
                             val lastName: String,
                             val phoneNumbers: String,
                             val borrowerDebt: Double,
                             val creditorDebt: Double
                             ) : Serializable {
                                 fun toContact() : Contact{
                                     return Contact(
                                         contactId,
                                         firstName,
                                         lastName,
                                         phoneNumbers
                                     )
                                 }
                             }

//storage
@Entity(
    foreignKeys = [
        ForeignKey(entity = Contact::class,
            parentColumns = ["contactId"],
            childColumns = ["contactId"],
            onUpdate = CASCADE,
            onDelete = CASCADE)
    ]
)
data class DebtRecordDb(@PrimaryKey(autoGenerate = true) var debtId: Int?,
                        var contactId: Int,
                        var debtAmount: Double,
                        var debtIsCreditor: Boolean,
                        var debtDescription: String,
                        var debtInitialDate : String,
                        var debtDueDate: String,
                        var interestRate: Double,
                        var interestType: String) : Serializable

@Entity
data class Contact(@PrimaryKey(autoGenerate = true) var contactId: Int?,
                   var firstName: String,
                   var lastName: String,
                   var phoneNumbers: String) : Serializable {
                        override fun toString(): String {
                            return "$firstName $lastName"
                        }
                   }

@Entity
data class History(@PrimaryKey(autoGenerate = true) var historyId: Int?, //pk
                   var debtId: Int,
                   var contactId: Int,
                   var name: String,
                   var desc: String,
                   var paymentAmount: Double,
                   var paymentDate: String,
                   var isFinal: Boolean) : Serializable

//select sum(paymentAmount) from history where debtId = :debtId