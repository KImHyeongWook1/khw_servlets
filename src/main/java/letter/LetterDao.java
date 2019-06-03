package letter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LetterDao {
	static final String ADD_LETTER = "insert letter(title,content,senderId,senderName,receiverId,receiverName) values(?,?,?,?,?,?)";

	static final String SEND_LIST_LETTERS = "select letterId, title, receiverId, receiverName, left(cdate,16) cdate from letter where senderId=? order by letterId desc limit ?,?";
	
	static final String RECEIVE_LIST_LETTERS = "select letterId, title, senderId, senderName, left(cdate,16) cdate from letter where receiverId=? order by letterId desc limit ?,?";

	static final String SEND_COUNT_LETTERS = "select count(letterId) from letter where senderId=?";
	
	static final String RECEIVE_COUNT_LETTERS = "select count(letterId) from letter where receiverId=?";

	static final String GET_LETTER = "select letterId, title, content, senderId, senderName, receiverId, receiverName, left(cdate,16) cdate from letter where letterId=? and (senderId=? or receiverId=?)";

	static final String DELETE_LETTER = "delete from letter where letterId=? and (receiverId=? or senderId=?)";
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	RowMapper<Letter> letterRowMapper = new BeanPropertyRowMapper<>(Letter.class);

	/**
	 * 보낸 목록
	 */
	public List<Letter> sendListLetters(String senderId, int offset, int count) {
		return jdbcTemplate.query(SEND_LIST_LETTERS, letterRowMapper, senderId, offset, count);
	}
	/*
	public List<Letter> sendListLetters(String senderId) {
		return jdbcTemplate.query(SEND_LIST_LETTERS, letterRowMapper, senderId);
	}
	*/
	
	/**
	 * 받은 목록
	 */
	public List<Letter> receiveListLetters(String receiverId, int offset, int count) {
		return jdbcTemplate.query(RECEIVE_LIST_LETTERS, letterRowMapper, receiverId, offset, count);
	}

	/**
	 * 보낸 메일 수
	 */
	public int getSendLettersCount(String senderId) {
		return jdbcTemplate.queryForObject(SEND_COUNT_LETTERS, Integer.class, senderId);
	}
	
	/**
	 * 받은 메일 수
	 */
	public int getReceiveLettersCount(String receiverId) {
		return jdbcTemplate.queryForObject(RECEIVE_COUNT_LETTERS, Integer.class, receiverId);
	}
	
	/**
	 * 메일 추가
	 */
	public int addLetter(Letter letter) {
		return jdbcTemplate.update(ADD_LETTER, letter.getTitle(), letter.getContent(), letter.getSenderId(),
				letter.getSenderName(), letter.getReceiverId(), letter.getReceiverName());
	}

	/**
	 * 메일 조회
	 */
	public Letter getLetter(String letterId, String memberId) {
		return jdbcTemplate.queryForObject(GET_LETTER, letterRowMapper, letterId, memberId, memberId);
	}

	/**
	 * 메일 삭제
	 */
	public int deleteLetter(String letterId, String memberId) {
		return jdbcTemplate.update(DELETE_LETTER, letterId, memberId, memberId);
	}
}