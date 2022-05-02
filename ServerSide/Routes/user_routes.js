const express = require("express");
const router = express.Router();

const User = require("../Controllers/users");
const authenticate = require("../Common/auth_middleware");

router.get("/:id", /*authenticate,*/ User.getFriendsList);

module.exports = router;
