<h1>QASITE APIs</h1>

<p>This project was created just to study more about building 
REST based apis in Jersey framework in Java</p>

<p>QASITE is "stackoverflow" clone with minimal features 
as of now.</p>

<h2>Apis Available</h3>

<h3>
1. Authentication
</h3>

<ul>
    <li><b>POST</b> /user/signup :- to create a user</li>
    <li><b>POST</b> /user/login :- to login to the site</li>
    <li><b>GET</b> /user/:id  :- to get user details</li>
    <li><b>PUT</b> /user/:id  :- to update the user details</li>
    <li><b>DELETE</b> /user/:id  :- to delete the user</li>
</ul>

<h3>2. Posting Questions and Answers</h3>

<ul>
    <li><b>POST</b> /questions  :- add a question(requires auth)</li>
    <li><b>PUT</b> /questions/:id  :- edit the question(requires auth)</li>
    <li><b>DELETE</b> /questions/:id  :- delete a question(requires auth)</li>
    <li><b>GET</b> /questions  :- fetches all questions</li>
    <li><b>GET</b> /questions/:id  :- fetches question by question id</li>
    <li><b>GET</b> /questions/author/:authorId  :- fetches question by particular author</li>
    <li><b>PATCH</b> /questions/:id/vote?direction=-1/1  :- upvote or downvote a question, -1 for downvote and 1 for upvote</li>
    <br/>
    <li><b>POST</b> /questions/:id/answer  :- answer a question(requires auth)</li>
    <li><b>PUT</b> /questions/:id/answer/:answerId  :- edit the answer(requires auth)</li>
    <li><b>DELETE</b> /questions/:id/answer/:id  :- delete the answer(requires auth)</li>
    <li><b>GET</b> /questions/:id/answers  :- fetches all answers to question id = id</li>
    <li><b>GET</b> /questions/:id/answers  :- fetches answer to a question by id</li>
    <li><b>GET</b> /questions/author/:id/answers  :- fetches answers by particular author</li>
    <li><b>PATCH</b> /questions/:id/answer/:answerId/vote?direction=-1/1  :- upvote or downvote an answer, -1 for downvote and 1 for upvote</li>

</ul>

<h2>Additional Features Planned</h2>

<ul>
    <li>Posting answer and allowing upvote/downvote on questions/answers only if the user has certain reputations(points)</li>
    <li>Statistics reporting for the user</li>
</ul>

<h2>Will update the db setup with details soon</h2>
