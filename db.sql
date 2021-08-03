create function update_qn_votes(userid integer, questionid integer, direction integer) returns boolean as $$
declare
	entry_exists integer
begin
	select count(*) into entry_exists from question_counts where userid=userid and questionid=questionid and direction=direction;
	if not found then 
		raise notice 'Entry already exists';
	else
		insert into question_counts(userid, questionid, direction) values(userid,questionid,direction);
		if direction == 1
			update questions set upvotes = upvotes + 1 where questionid = questionid;
		else
			update questions set downvotes = downvotes + 1 where questionid = questionid;
	end if;
	return true;