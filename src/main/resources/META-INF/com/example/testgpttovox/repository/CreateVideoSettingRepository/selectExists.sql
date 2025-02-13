select
  exists
(
  select
    *
  from
    mstknr.create_video_setting
  where
    userid = /* id */''
)