<#-- @ftlvariable name="" type="com.kyl.cheque.views.ChequesView" -->
<#include "commons/common.ftl">
<html>
<head>
    <title>Cheques</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="/assets/css/bootstrap.min.css" rel="stylesheet">
    <@commonStyle/>
</head>

<body>

<div id="wrap">

    <div class="container-narrow">
        <div class="masthead">
            <ul class="nav nav-pills pull-right">
                <li class="active"><a href="/cheque/view/all">Home</a></li>
                <li><a href="/cheque/view/create">Create a cheque</a></li>
                <li><a href="#about" role="button" class="btn" data-toggle="modal">About</a></li>
            </ul>
            <h3 class="text-primary"><a href="/cheque/view/all">Cheque</a></h3>
        </div>

        <@aboutModal/>

        <br>

    <#if cheques??>
        <table id="chequeTable" class="table table-bordered table-striped">
            <thead>
            <tr>
                <th>Cheque Id</th>
                <th>Recipient</th>
                <th>Amount</th>
                <th>Payment Date</th>
                <th>Amount Description</th>
                <th>Created Date</th>
                <th>Last Modified</th>
            </tr>
            </thead>
            <tbody>
                <#list cheques as cheque>
                <tr>
                    <td>
                        <a href="/cheque/view/id/${cheque.getChequeId()?string("0")}">${cheque.getChequeId()?string("0")}</a>
                    </td>
                    <td><a href="/cheque/view/recipient/${cheque.getRecipient()}">${cheque.getRecipient()}</a></td>
                    <td>${cheque.getAmount()}</td>
                    <td>${cheque.getPaymentDate()}</td>
                    <td>${cheque.getAmountDesc()}</td>
                    <td>${cheque.getAddedTime()}</td>
                    <td>${cheque.getUpdatedTime()}</td>
                </tr>
                </#list>
            </tbody>
        </table>
    </#if>
    </div>
</div>

<@chequeFooter/>

<script src="/assets/js/jquery-2.2.3.min.js"></script>
<script src="/assets/js/bootstrap.min.js"></script>
</body>
</html>
