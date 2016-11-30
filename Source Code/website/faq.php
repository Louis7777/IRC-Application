<?php

$page_title = "Java Web Chat - Frequently Asked Questions";
$current_file = basename($_SERVER['PHP_SELF']);
include_once("./includes/header.php");
?>
<section id="content">
    <div style="text-align: left; margin: 50px 0 0 300px;">
        <br /><br /><br /><br />
        <h1>Frequently Asked Questions</h1>
        <br /><br /><br />
        <p ><strong>Q:</strong> How to embed the Applet in my website?</p>
        <br /><br />
        <p><strong>Q:</strong> Use the code below:<br /><br />
        <pre>
    &lt;object type="application/x-java-applet" width="480" height="640"&gt;
        &lt;param name="code" value="javawebchat/JavaWebChat" /&gt;
        &lt;param name="archive" value="JavaWebChat.jar" /&gt;
        &lt;param name="java_arguments" value="-Djnlp.packEnabled=true"/&gt;
        &lt;param name="NICKNAME" value="Guest" /&gt;
        &lt;param name="SERVER" value="localhost" />
        Applet failed to run.  No Java plug-in was found.
    &lt;/object&gt;
        </pre>
        <br /><br />
        Replace the "NICKNAME" and "SERVER" values with your preferred nickname and server.
        </p>
    </div>
</section>
<?php

include_once("./includes/footer.php");
?>