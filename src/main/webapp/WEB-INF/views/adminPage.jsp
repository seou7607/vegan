<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>관리자 페이지</title>

    <style>
    .none {
      display: none !important;
   }
   .onlyAdmin {
      display: block !important;
   }
   .borderBottom {
        border-bottom: solid 1px #49c5a2;
   }
   
        .btn-list {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
            height: 100%;
        }
        .btn-list .btn {
            display: block;
            width: 200px;
            height: 200px;
            margin: 10px;
            background-color: #fff;
            border: 1px solid #ddd;
            text-align: center;
            line-height: 200px;
            font-size: 24px;
            font-weight: bold;
            text-decoration: none;
            color: #333;
        }
        .btn-list .btn:hover {
            background-color: #eee;
        }
    </style>
</head>
<body>
    <%@ include file="./header.jsp" %>
    <div class="contentWrap mt-5">
        <div class="contentBox">
            <div class="row mb-2 text-center">
                <div class="col">
                    <div class="btn-list">
                        <a href="./admin.userList" class="btn">유저 리스트</a>
                        <a href="./admin.reportList" class="btn">신고 리스트</a>
                        <a href="./recieveletter.go" class="btn">관리자 쪽지</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>


</html>
