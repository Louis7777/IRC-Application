<?php

$page_title = "Java Web Chat - Frequently Asked Questions";
$current_file = basename($_SERVER['PHP_SELF']);
include_once("./includes/header.php");
?>
<section id="content">
    <div style="text-align: left; margin: 60px 0 0 200px;">
        <h3>IRC Commands</h3>
        <br /><br />
        <ul>
            <li><a href="#join">JOIN</a></li>
            <li><a href="#nick">NICK</a></li>
            <li><a href="#part">PART</a></li>
            <li><a href="#privmsg">PRIVMSG</a></li>
            <li><a href="#topic">TOPIC</a></li>
        </ul>
        <br /><br /><br />
        <div>
            <a name="join">JOIN</a> <div style=" color: #0066ff">/join &lt;#channel&gt;</div>
            <article style="background-color: #fff; border-radius: 5px; margin: 15px 0 15px 0; padding: 5px;">
                Use it to join a channel or a list of comma separated channels. Examples:<br /><br />

                /join #IST_university<br />
                /join #Chatroom1,#Chatroom2,Chatroom3<br /><br />

                Note: A channel name must begin with a number sign (#) and should not contain any spaces.
            </article>
            <a name="nick">NICK</a> <div style=" color: #0066ff">/nick &lt;nickname&gt;</div>
            <article style="background-color: #fff; border-radius: 5px; margin: 15px 0 15px 0; padding: 5px;">
                Use it to change your nickname while you are chatting. Type "/nick", leave a space and type your new nickname. Example:<br /><br />

                /nick Louis<br /><br />

                Note: A nickname can contain any letter, number, or any of the following characters: - _ [ ] | ^ ` { } "
                However, it cannot start with a number and most servers allow nicknames up to 15 characters long.
            </article>
            <a name="part">PART</a> <div style=" color: #0066ff">/part &lt;#channel&gt; &lt;reason&gt;</div>
            <article style="background-color: #fff; border-radius: 5px; margin: 15px 0 15px 0; padding: 5px;">
                Use it to leave from a channel that you are already a member of. Examples:<br /><br />

                /part #IST_university<br />
                /part #Chatroom1,#Chatroom2,Chatroom3
            </article>
            <a name="privmsg">PRIVMSG</a> <div style=" color: #0066ff">/privmsg &lt;#channel&gt;|&lt;nickname&gt; &lt;message&gt;</div>
            <article style="background-color: #fff; border-radius: 5px; margin: 15px 0 15px 0; padding: 5px;">
                Use it to send a message to a channel or to another user. Examples:<br /><br />

                /privmsg #athens Hello everyone!<br />
                /privmsg Maria Hey Maria, how's it going?
            </article>
            <a name="topic">TOPIC</a> <div style=" color: #0066ff">/topic &lt;#channel&gt; &lt;topic&gt;</div>
            <article style="background-color: #fff; border-radius: 5px; margin: 15px 0 15px 0; padding: 5px;">
                Use it to see or set a channel's topic. Examples:<br /><br />

                /topic #Cafeteria<br />
                /topic #Cafeteria Have a nice time chatting in Cafeteria!
            </article>
        </div>
        <br />
    </div>
</section>
<?php

include_once("./includes/footer.php");
?>