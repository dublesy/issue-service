package dublesy.issueservice.service

import dublesy.issueservice.domain.Issue
import dublesy.issueservice.domain.IssueRepository
import dublesy.issueservice.domain.enums.IssueStatus
import dublesy.issueservice.exception.NotFoundException
import dublesy.issueservice.model.IssueRequest
import dublesy.issueservice.model.IssueResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueService(
    private val issueRepository: IssueRepository,
) {

    @Transactional
    fun create(userId: Long, request: IssueRequest): IssueResponse {

        val issue = Issue(
            summary = request.summary,
            description = request.description,
            userId = userId,
            type = request.type,
            priority = request.priority,
            status = request.status,
        )

        return IssueResponse(issueRepository.save(issue))

    }

    @Transactional(readOnly = true)
    fun getAll(status: IssueStatus)=
        issueRepository.findAllByStatusOrderByCreatedAtDesc(status)
            ?.map{
                IssueResponse(it)
            }

    @Transactional(readOnly = true)
    fun get(id: Long): IssueResponse {
        val issue = issueRepository.findByIdOrNull(id) ?: throw NotFoundException("이슈가 존재하지 않습니다.")
        return IssueResponse(issue)
    }

    @Transactional
    fun edit(userId: Long, id: Long, request: IssueRequest): IssueResponse {
        val issue: Issue = issueRepository.findByIdOrNull(id) ?: throw NotFoundException("이슈가 존재하지 않습니다.")

        return with(issue) {
            summary = request.summary
            description = request.description
            this.userId = userId
            type = request.type
            priority = request.priority
            status = request.status
            // dirty checking을 하기 때문에 저장이 되지만 명시적으로 save함수를 사용해서 처리한다.
            IssueResponse(issueRepository.save(this))
        }

    }

    fun delete(id: Long) {
        issueRepository.deleteById(id)
    }
}