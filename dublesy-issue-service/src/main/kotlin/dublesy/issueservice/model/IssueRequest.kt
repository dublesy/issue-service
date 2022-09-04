package dublesy.issueservice.model

import dublesy.issueservice.domain.enums.IssuePriority
import dublesy.issueservice.domain.enums.IssueStatus
import dublesy.issueservice.domain.enums.IssueType

data class IssueRequest(
    val summary: String,
    val description: String,
    val type: IssueType,
    val priority: IssuePriority,
    val status: IssueStatus,
)