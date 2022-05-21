const express = require("express");
const router = express.Router();

const User = require("../Controllers/users");
const authenticate = require("../Common/auth_middleware");

router.get("/:id", /*authenticate,*/ User.getFriendsList);
router.get("/second/:id", /*authenticate,*/ User.getSecondCircleOnly);
router.get("/third/:id", /*authenticate,*/ User.getThirdCircleOnly);
router.get(
  "/addFriendToMyContacts/:myId/:hisId",
  /*authenticate,*/ User.addFriendToMyContacts
);
router.get(
  "/removeFriendFromMyContacts/:myId/:hisId",
  /*authenticate,*/ User.removeFriendFromMyContacts
);
router.post("/rateMyHelp/:id", User.rateMyHelp);


module.exports = router;
