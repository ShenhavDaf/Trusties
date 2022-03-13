const express = require("express");
const router = express.Router();

const Auth = require("../Controllers/auth");

router.post("/login", Auth.login);
router.post("/register", Auth.register);
router.post("/logout", Auth.logout);
router.post("/resendEmail", Auth.resendEmail);
router.post("/afterVerify", Auth.verifiedUser);
// router.get("/verify", Auth.verifyRandomCode);

module.exports = router;
