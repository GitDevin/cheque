<#macro commonStyle>
<style type="text/css">
    body {
        padding-top: 20px;
        padding-bottom: 40px;
    }

    /* Custom container */
    .container-narrow {
        margin: 0 auto;
        max-width: 700px;
    }

    .container-narrow > hr {
        margin: 30px 0;
    }

    /* Sticky footer styles
  -------------------------------------------------- */

    html,
    body {
        height: 100%;
        /* The html and body elements cannot have any padding or margin. */
    }

    /* Wrapper for page content to push down footer */
    #wrap {
        min-height: 100%;
        height: auto !important;
        height: 100%;
        /* Negative indent footer by it's height */
        margin: 0 auto -60px;
    }

    /* Set the fixed height of the footer here */
    #push,
    #footer {
        height: 60px;
    }

    #footer {
        background-color: #f5f5f5;
    }

    /* Lastly, apply responsive CSS fixes as necessary */
    @media (max-width: 767px) {
        #footer {
            margin-left: -20px;
            margin-right: -20px;
            padding-left: 20px;
            padding-right: 20px;
        }
    }
</style>
</#macro>


<#macro aboutModal>
<div id="about" class="modal fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            </div>
            <div class="modal-body">
                <p>This is a simple micro service demo.</p>
                <p>Source code can be found at <a href="https://github.com/gitdevin/cheque">
                    https://github.com/gitdevin/cheque</a>.</p>
                <p>You can contact author of this demo by
                    <a href="mailto:dev.kun.yu.liu+chequedemo@gmail.com?Subject=Cheque%20demo">email</a>.</p>
            </div>
        </div>
    </div>
</div>
</#macro>

<#macro chequeFooter>
<div id="footer" class="container">
    <p class="muted credit">Source code can be found at <a href="https://github.com/gitdevin/cheque">
        https://github.com/gitdevin/cheque</a>.</p>
</div>
</#macro>
