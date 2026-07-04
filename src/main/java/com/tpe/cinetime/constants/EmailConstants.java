package com.tpe.cinetime.constants;

public class EmailConstants {

    private EmailConstants(){};

    //ubject Lines
    public static final String PASSWORD_RESET_SUBJECT   = "CineTime - Password Reset Request";
    public static final String PASSWORD_CHANGED_SUBJECT = "CineTime - Password Changed Successfully";

    //Password Reset Email
    //Format args: (1) resetToken, (2) tokenValidityMinutes
    public static final String PASSWORD_RESET_BODY_HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <title>Password Reset</title>
                <style>
                    body { margin:0; padding:0; background-color:#0f0f0f; font-family:'Segoe UI',Arial,sans-serif; color:#f0f0f0; }
                    .wrapper { max-width:600px; margin:40px auto; background-color:#1a1a1a; border-radius:12px; overflow:hidden; border:1px solid #2e2e2e; }
                    .header { background:linear-gradient(135deg,#b30000,#e50914); padding:36px 40px; text-align:center; }
                    .header h1 { margin:0; font-size:28px; font-weight:800; letter-spacing:3px; color:#ffffff; text-transform:uppercase; }
                    .header p { margin:6px 0 0; font-size:13px; color:rgba(255,255,255,0.75); letter-spacing:1px; }
                    .body { padding:40px; }
                    .body p { font-size:15px; line-height:1.7; color:#cccccc; margin:0 0 20px; }
                    .token-box { background-color:#111111; border:1px dashed #e50914; border-radius:8px; padding:20px 24px; text-align:center; margin:28px 0; }
                    .token-box p { margin:0 0 8px; font-size:12px; color:#888888; text-transform:uppercase; letter-spacing:1px; }
                    .token-box code { font-size:14px; color:#e50914; word-break:break-all; font-family:monospace; }
                    .instruction-box { background-color:#1e1e1e; border-radius:8px; padding:20px 24px; margin:0 0 24px; }
                    .instruction-box p { margin:0 0 8px; font-size:13px; color:#aaaaaa; }
                    .instruction-box code { background:#2a2a2a; color:#e0e0e0; padding:2px 6px; border-radius:4px; font-size:13px; }
                    .warning-box { background-color:#2a1a1a; border-left:4px solid #e50914; border-radius:6px; padding:16px 20px; margin-top:24px; }
                    .warning-box p { margin:4px 0; font-size:13px; color:#aaaaaa; }
                    .warning-box p strong { color:#e50914; }
                    .footer { text-align:center; padding:24px 40px; border-top:1px solid #2e2e2e; font-size:12px; color:#555555; }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="header">
                        <h1>&#127916; CineTime</h1>
                        <p>Password Reset Request</p>
                    </div>
                    <div class="body">
                        <p>Hello,</p>
                        <p>We received a request to reset your <strong style="color:#e50914">CineTime</strong> account password. Use the token below to reset it.</p>
 
                        <div class="token-box">
                            <p>Your Reset Token (copy this)</p>
                            <code>%s</code>
                        </div>
 
                        <div class="instruction-box">
                            <p>Send a <code>POST</code> request to <code>/auth/reset-password</code> with the following body:</p>
                            <p><code style="white-space:pre-line; display:block; margin-top:8px; line-height:1.8;">{
                  "resetPasswordToken": "paste-token-here",
                  "newPassword": "YourNewPassword123@",
                  "confirmPassword": "YourNewPassword123@"
                }</code></p>
                        </div>
 
                        <div class="warning-box">
                            <p>&#9201; <strong>Expires in:</strong> %d minutes</p>
                            <p>&#128274; <strong>One-time use only</strong></p>
                            <p>&#128683; If you didn't request this, you can safely ignore this email.</p>
                        </div>
                    </div>
                    <div class="footer">
                        Stay secure &mdash; The CineTime Team<br/>
                        <span>This is an automated message, please do not reply.</span>
                    </div>
                </div>
            </body>
            </html>
            """;

    //Password Changed Confirmation Email
    //Format args: (1) changedAt (datetime string)
    public static final String PASSWORD_CHANGED_BODY_HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <title>Password Changed</title>
                <style>
                    body { margin:0; padding:0; background-color:#0f0f0f; font-family:'Segoe UI',Arial,sans-serif; color:#f0f0f0; }
                    .wrapper { max-width:600px; margin:40px auto; background-color:#1a1a1a; border-radius:12px; overflow:hidden; border:1px solid #2e2e2e; }
                    .header { background:linear-gradient(135deg,#145214,#1e7e1e); padding:36px 40px; text-align:center; }
                    .header h1 { margin:0; font-size:28px; font-weight:800; letter-spacing:3px; color:#ffffff; text-transform:uppercase; }
                    .header p { margin:6px 0 0; font-size:13px; color:rgba(255,255,255,0.75); letter-spacing:1px; }
                    .body { padding:40px; }
                    .body p { font-size:15px; line-height:1.7; color:#cccccc; margin:0 0 20px; }
                    .success-box { background-color:#0f1f0f; border-left:4px solid #1e7e1e; border-radius:6px; padding:16px 20px; margin:24px 0; }
                    .success-box p { margin:4px 0; font-size:14px; color:#aaaaaa; }
                    .success-box p strong { color:#2ea82e; }
                    .warning-box { background-color:#2a1a1a; border-left:4px solid #e50914; border-radius:6px; padding:16px 20px; margin-top:8px; }
                    .warning-box p { margin:4px 0; font-size:13px; color:#aaaaaa; }
                    .warning-box p strong { color:#e50914; }
                    .footer { text-align:center; padding:24px 40px; border-top:1px solid #2e2e2e; font-size:12px; color:#555555; }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="header">
                        <h1>&#127916; CineTime</h1>
                        <p>Password Changed Successfully</p>
                    </div>
                    <div class="body">
                        <p>Hello,</p>
                        <p>Your <strong style="color:#2ea82e">CineTime</strong> account password has been changed successfully.</p>
 
                        <div class="success-box">
                            <p>&#9989; <strong>Password updated</strong></p>
                            <p>&#128336; <strong>Time:</strong> %s</p>
                        </div>
 
                        <div class="warning-box">
                            <p>&#128680; <strong>Wasn't you?</strong> Contact us immediately and secure your account.</p>
                        </div>
                    </div>
                    <div class="footer">
                        Stay secure &mdash; The CineTime Team<br/>
                        <span>This is an automated message, please do not reply.</span>
                    </div>
                </div>
            </body>
            </html>
            """;
}
