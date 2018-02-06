<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.net.Socket"%>
<%@ page language="java" import="java.net.SocketException"%>
<%@ page language="java" import="java.net.InetSocketAddress"%>
<%@ page language="java" import="java.net.URL"%>
<%@ page import="net.sf.json.*,java.io.*" %>


    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
      <head>
        <title>My JSP 'Output.jsp' starting page</title>
      </head>

      <body>
         <%
         response.setContentType("text/html;charset=utf-8");
         request.setCharacterEncoding("utf-8");
         String reqData = request.getParameter("inputText");
         reqData = new String(reqData.getBytes("iso8859-1"),"utf-8");

         String reqURL = request.getParameter("urlText");

         OutputStream out2 = null;
         InputStream in = null;
         Socket socket = null;
         String respMsg = null;
         String respCharset = "UTF-8";
         StringBuilder reqMsg = new StringBuilder();

         try {
             URL sendURL = new URL(reqURL);
             String host = sendURL.getHost();
             int port = sendURL.getPort() == -1 ? 80 : sendURL.getPort();
             socket = new Socket();
             socket.setTcpNoDelay(true);
             socket.setReuseAddress(true);
             socket.setSoTimeout(30000);
             socket.setSoLinger(true, 5);
             socket.setSendBufferSize(1024);
             socket.setReceiveBufferSize(1024);
             socket.setKeepAlive(true);
             socket.setOOBInline(true);
             socket.setTrafficClass(0x04 | 0x10);
             socket.setPerformancePreferences(2, 1, 3);
             socket.connect(new InetSocketAddress(host, port), 30000);

             String[] tempString = reqData.split("\r");
             for (String item : tempString) {
                 reqMsg.append(item).append("\r\n");
             }

             out2 = socket.getOutputStream();
             out2.write(reqMsg.toString().getBytes());

             in = socket.getInputStream();
             ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
             byte[] buffer = new byte[512];
             int len = -1;

             while ((len = in.read(buffer)) != -1) {
                 bytesOut.write(buffer, 0, len);
             }

             respMsg = bytesOut.toString(respCharset);
         } catch (SocketException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             if (null != socket && socket.isConnected() && !socket.isClosed()) {
                 try {
                     socket.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }

         PrintWriter pw = response.getWriter();


         StringBuilder output = new StringBuilder();
         output.append(reqMsg);
         output.append("\r\n");
         output.append("---------------------------------------------------------------");
         output.append("\r\n");
         output.append(respMsg);

         pw.print(output);

         pw.close();


      %>
      </body>
    </html>