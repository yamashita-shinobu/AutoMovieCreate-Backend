select
  user_id,
  video_id,
  bms_name,
  cmt_frame_name,
  bgm_name,
  comments,
  main_images
from
  mstknr.edit_video_info
where
  user_id = /* userId */''
and
  video_id = /* videoId */''