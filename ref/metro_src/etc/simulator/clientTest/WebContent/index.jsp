<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
      <head>
         <title>My JSP 'index.jsp' starting page</title>
        <script type="text/javascript" language="javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript">
      function CheckAjax ()
      {
     	if($('#inputText').text().length==0){
                       $('.hint').text("please input text").css({"background-color":"red"});
        }
        else{
        	$('.hint').text("");
        	$.ajax({
                type: "get",
                dataType: "text",
                url: "Output.jsp",
                contentType: "text/html;charset=utf-8",
                data: {inputText:$('#inputText').text(), urlText:$('#url').val()},
                beforeSend: function() {
                $("span").html("<font color='red'>ajax text</font>");},

                complete :function(){$("span").html("<font color='red'>post complete!</font>");},
                success: function(data){
                    alert("success");
                    $('#outputText').text(data);

                },
                error: function(e) {alert("error");}
             });

         }
      }
    </script>
      </head>

      <body>
         <table>
         <tr><td>URL<br/>
         <input id="url" name="url" style="width:800px;"></input>
         </td></tr>
         <tr><td>inputText<br/>
         <textarea id="inputText" name="inputText" title="inputText" onblur="" style="width:800px;height:200px;"></textarea>
         </td></tr>
         <tr><td>
           <div class="hint">   </div>
         </td></tr>

         <tr><td>
         <span></span>
         </td></tr>
         <tr><td>outputText<br/>
         <textarea id="outputText" name="outputText" title="outputText" style="width:800px;height:400px;"></textarea>
         <button onClick="CheckAjax();">POST</button>
         </td></tr>
		<tr><td>POST /opal-web/find/json HTTP/1.1<br/>
		Host: localhost<br/>
		Content-Type: application/json; charset=UTF-8<br/>
		Accept: application/json; charset=UTF-8<br/>
		User-Agent: JavaSocket/1.8.0_121<br/>
		Content-Length: 47<br/>
		Cache-Control: no-cache<br/>
		Pragma: no-cache<br/>
		Connection: keep-alive<br/>
<br/>
		{"userId":1,"kanjiName":"aaa","kanaName":"123"}</td></tr>
         </table>
      </body>
    </html>