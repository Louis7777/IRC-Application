<?php

$page_title = "Java Web Chat";
$current_file = basename($_SERVER['PHP_SELF']);
include_once("./includes/header.php");
?>
<section id="content">
    <br /><br /><br />
    <object type="application/x-java-applet" width="480" height="640">
        <param name="code" value="javawebchat/JavaWebChat" />
        <param name="archive" value="JavaWebChat.jar" />
        <param name="java_arguments" value="-Djnlp.packEnabled=true"/>
        <param name="NICKNAME" value="Guest" />
        <param name="SERVER" value="localhost" />
        Applet failed to run.  No Java plug-in was found.
    </object>
</section>
<?php

include_once("./includes/footer.php");
?>