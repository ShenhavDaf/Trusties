const express = require("express");
const router = express.Router();

const Auth = require("../Controllers/auth");

router.post("/login", Auth.login);
router.post("/register", Auth.register);
router.post("/logout", Auth.logout);
// router.post("/verify", Auth.verifyEmail);
router.get("/verify", Auth.verifyRandomCode);
module.exports = router;
