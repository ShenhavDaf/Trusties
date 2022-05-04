const express = require("express");
const router = express.Router();

const User = require("../Controllers/users");
const authenticate = require("../Common/auth_middleware");

router.get("/:id", /*authenticate,*/ User.getFriendsList);
router.get("/second/:id", /*authenticate,*/ User.getSecondCircleOnly);
router.get("/third/:id", /*authenticate,*/ User.getThirdCircleOnly);
router.get(
  "/addFriend/:myId/:hisId",
  /*authenticate,*/ User.addFriendToMyContacts
);

module.exports = router;
