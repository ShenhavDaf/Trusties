const express = require("express");
const router = express.Router();

const User = require("../Controllers/users");
const authenticate = require("../Common/auth_middleware");

router.get("/getFeed/:id", authenticate, User.getMyRelatedPosts);
router.get("/:id", /*authenticate,*/ User.getFriendsList);
router.get("/second/:id", /*authenticate,*/ User.getSecondCircleOnly);
router.get("/third/:id", /*authenticate,*/ User.getThirdCircleOnly);
router.get("/addFriendToMyContacts/:myId/:hisId", User.addFriendToMyContacts);
router.get(
  "/removeFriendFromMyContacts/:myId/:hisId",
  User.removeFriendFromMyContacts
);
router.post("/rateMyHelp/:id", User.rateMyHelp);
router.get("/getRating/:id", User.getRating);

module.exports = router;
