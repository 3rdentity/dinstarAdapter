<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dinstar Status</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="css/bootstrap.min.css" crossorigin="anonymous">
    <%--<link rel="stylesheet" href="css/dataTables.bootstrap.css">--%>
</head>
<body class="body-img">
    <table id="example" class="table table-hover display">
        <thead>
            <tr class="active">
                <th> Estado API </th>
                <c:forEach items="${status}" var="stat">
                    <th>${stat.getKey()}</th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td> ${apiStatus} </td>
                <c:forEach items="${status}" var="stat">
                        <td> ${stat.getValue()} </td>
                </c:forEach>
            </tr>
        </tbody>
    </table>
    <!-- Scripts -->
    <script src="js/jquery-2.1.3.min.js"></script>
    <%--<script src="js/jquery.dataTables.min.js"></script>
    <script src="js/dataTables.bootstrap.js"></script>
    <script>
        $(document).ready(function() {
            $("#example").dataTable({
                stateSave: true
            });
        })
    </script>--%>
</body>
</html>