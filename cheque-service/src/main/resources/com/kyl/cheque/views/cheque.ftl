<#-- @ftlvariable name="" type="com.kyl.cheque.views.ChequeView" -->
<#include "commons/common.ftl">
<html>
<head>
    <title>Cheques</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script src="/assets/js/jquery-2.2.3.min.js"></script>
    <script src="/assets/js/bootstrap.min.js"></script>
    <link href="/assets/css/cheque.css" rel="stylesheet">
    <link href="/assets/css/bootstrap.min.css" rel="stylesheet">
<@commonStyle/>
</head>

<body>
<div id="wrap">
    <div class="container-narrow">
        <div class="masthead">
            <ul class="nav nav-pills pull-right">
                <li><a href="/cheque/view/all">Home</a></li>
            <#if getIfReadOnly()>
                <li><a href="/cheque/view/create">Create a cheque</a></li>
            <#else>
                <li class="active"><a href="/cheque/view/create">Create a cheque</a></li>
            </#if>
                <li><a href="#about" role="button" class="btn" data-toggle="modal">About</a></li>
            </ul>
            <h3 class="text-primary"><a href="/cheque/view/all">Cheque</a></h3>
        </div>

    <@aboutModal/>

        <br>

        <div id="cheque-image-container">
            <img id="cheque-image" src="/assets/cheque.jpg" height="462" width="900"/>
        <#if getIfReadOnly()>
            <p id="cheque-image-payment-date-text">${getCheque().getPaymentDate()}</p>
            <p id="cheque-image-amount-text">${getCheque().getAmount()}</p>
            <p id="cheque-image-Recipient-text">
                <a href="/cheque/view/recipient/${getCheque().getRecipient()}">${getCheque().getRecipient()}</a>
            </p>
            <p id="cheque-image-amount-desc-text">${getCheque().getAmountDesc()}</p>
        <#else>
            <form>
                <input id="cheque-image-payment-date-input" type="date" name="cheque-input-payment-date">
                <input id="cheque-image-dollar-input" type="text" name="cheque-input-dollar" size="10">
                <p id="cheque-image-amount-dot">.</p>
                <input id="cheque-image-cent-input" type="text" name="cheque-input-cent" size="10">
                <input id="cheque-image-recipient-input" type="text" name="cheque-input-recipient">
                <p id="cheque-image-amount-desc-text"></p>
            </form>
        </#if>
        </div>
    <#if !getIfReadOnly()>
        <div>
            <button id="cheque-submit" type="submit" class="btn btn-large btn-success">Create cheque</button>
        </div>
        <div id="create-cheque-error-alert" class="alert alert-danger fade in hide">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <h4 class="alert-heading">Failed to create the cheque!</h4>
            <p>Please fix following errors:</p>
            <p id="cheque-error-msg"></p>
        </div>
        <div id="amount-description-alert" class="alert alert-warning fade in hide">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <p id="amount-error-msg"></p>
        </div>
    </#if>
    </div>
</div>

<@chequeFooter/>

<script>
    $(document).ready(function () {
        $("#cheque-submit").on('click', function (e) {
            var response = $.ajax({
                type: "PUT",
                url: "/cheque/service/put",
                contentType: "application/json",
                data: JSON.stringify({
                    "dollar": $("#cheque-image-dollar-input").val(),
                    "cent": $("#cheque-image-cent-input").val(),
                    "recipient": $("#cheque-image-recipient-input").val(),
                    "paymentDate": $("#cheque-image-payment-date-input").val()
                }),
                dataType: 'html',
                async: true,
                success: function (data, textStatus, jqXHR) {
                    var chequeId = jqXHR.getResponseHeader('location').split("/").slice(-1)[0];
                    if (chequeId) {
                        window.location.href = "/cheque/view/id/" + chequeId;
                    }
                },
                error: function (xhr, textStatus, error) {
                    if (xhr.responseText) {
                        var err = JSON.parse(xhr.responseText);
                        $("#cheque-error-msg").text(err.error);
                    }
                    $('#create-cheque-error-alert').removeClass('hide');
                    $('#amount-description-alert').addClass('hide');
                }
            });
        });

        function changeAmountDescription() {
            var response = $.ajax({
                type: "POST",
                url: "/money/service/format",
                contentType: "application/json",
                data: JSON.stringify({
                    "dollar": $("#cheque-image-dollar-input").val(),
                    "cent": $("#cheque-image-cent-input").val()
                }),
                dataType: 'html',
                async: true,
                success: function (data, textStatus, jqXHR) {
                    var result = JSON.parse(jqXHR.responseText);
                    $("#cheque-image-amount-desc-text").text(result.result);
                    $('#amount-description-alert').addClass('hide');
                },
                error: function (xhr, textStatus, error) {
                    var err = JSON.parse(xhr.responseText);
                    $("#amount-error-msg").text(err.error);
                    $('#amount-description-alert').removeClass('hide');
                }
            });
        }

        $("#cheque-image-dollar-input").change(function (e) {
            changeAmountDescription()
        });

        $("#cheque-image-cent-input").change(function (e) {
            changeAmountDescription()
        });

        $("#cheque-image-dollar-input").blur(function (e) {
            changeAmountDescription()
        });

        $("#cheque-image-cent-input").blur(function (e) {
            changeAmountDescription()
        });
    });
</script>
</body>
</html>
