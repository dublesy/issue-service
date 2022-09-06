package dublesy.issueservice.model

import dublesy.issueservice.domain.Comment

data class CommentResponse (
    val id : Long,
    val issueId: Long,
    val userId: Long,
    val body: String,
    val username: String? = null
)

fun Comment.toResponse() = CommentResponse(
    id = id!!,
    issueId = issue.id!!,
    userId = userId,
    body = body,
    username = username,
)