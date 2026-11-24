package com.example.scholarhub.data.user

import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.feature.profile.Achievement
import com.example.scholarhub.feature.profile.MenteeProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val uid: String? get() = auth.currentUser?.uid

    suspend fun saveProfile(profile: MenteeProfile): Boolean {
        val userId = uid ?: return false
        return try {
            val data = mapOf(
                "name" to profile.name,
                "degree" to profile.degree,
                "campus" to profile.campus,
                "major" to profile.major,
                "semester" to profile.semester,
                "gpa" to profile.gpa,
                "ieltsType" to profile.ieltsType,
                "ieltsScore" to profile.ieltsScore,
                "toeflType" to profile.toeflType,
                "toeflScore" to profile.toeflScore,
                "achievements" to profile.achievements.map {
                    mapOf(
                        "id" to it.id,
                        "title" to it.title,
                        "level" to it.level,
                        "year" to it.year,
                        "organizer" to it.organizer
                    )
                }
            )
            firestore.collection("users").document(userId).set(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getProfile(): MenteeProfile? {
        val userId = uid ?: return null
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (!doc.exists()) return null

            val achievementsList = (doc.get("achievements") as? List<Map<String, Any>>)?.mapNotNull { map ->
                Achievement(
                    id = map["id"] as? String ?: return@mapNotNull null,
                    title = map["title"] as? String ?: "",
                    level = map["level"] as? String ?: "",
                    year = (map["year"] as? Number)?.toInt() ?: 0,
                    organizer = map["organizer"] as? String ?: ""
                )
            } ?: emptyList()

            MenteeProfile(
                name = doc.getString("name") ?: "Rayhan Pratama",
                photo = null, // URI dari memori lokal HP tidak disimpan ke Firestore
                degree = doc.getString("degree"),
                campus = doc.getString("campus"),
                major = doc.getString("major"),
                semester = doc.getLong("semester")?.toInt(),
                gpa = doc.getString("gpa") ?: "",
                ieltsType = doc.getString("ieltsType"),
                ieltsScore = doc.getString("ieltsScore") ?: "",
                toeflType = doc.getString("toeflType"),
                toeflScore = doc.getString("toeflScore") ?: "",
                achievements = achievementsList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveMatchInput(input: MatchInput): Boolean {
        val userId = uid ?: return false
        return try {
            val data = mapOf(
                "activeStudent" to input.activeStudent,
                "degree" to input.degree,
                "campus" to input.campus,
                "campusType" to input.campusType,
                "major" to input.major,
                "semester" to input.semester,
                "gpa" to input.gpa?.toDouble(), // Konversi BigDecimal ke Double agar didukung Firebase
                "noGpa" to input.noGpa,
                "organization" to input.organization,
                "receivingScholarship" to input.receivingScholarship,
                "commitment" to input.commitment,
                "priorities" to input.priorities.toList()
            )
            firestore.collection("users").document(userId)
                .collection("scholar_match").document("latest_input")
                .set(data)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getMatchInput(): MatchInput? {
        val userId = uid ?: return null
        return try {
            val doc = firestore.collection("users").document(userId)
                .collection("scholar_match").document("latest_input")
                .get().await()
            if (!doc.exists()) return null

            MatchInput(
                activeStudent = doc.getBoolean("activeStudent"),
                degree = doc.getString("degree"),
                campus = doc.getString("campus"),
                campusType = doc.getString("campusType"),
                major = doc.getString("major"),
                semester = doc.getLong("semester")?.toInt(),
                gpa = doc.getDouble("gpa")?.toBigDecimal(), // Konversi balik ke BigDecimal
                noGpa = doc.getBoolean("noGpa") ?: false,
                organization = doc.getBoolean("organization"),
                receivingScholarship = doc.getBoolean("receivingScholarship"),
                commitment = doc.getBoolean("commitment"),
                priorities = (doc.get("priorities") as? List<String>)?.toSet() ?: emptySet()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
